package template;

import com.erikmolssons.template.JDBCTemplate;
import com.zaxxer.hikari.HikariConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;

class JDBCTemplateTest {

    static JDBCTemplate template;

    @BeforeAll
    static void beforeAll() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;INIT=runscript from 'src/test/resources/Employee.sql'");
        config.setUsername("sa");
        config.setPassword("");
        template = new JDBCTemplate(config);
    }

    @Test
    void testQueryForList() {
        var l = template.queryForList("select * from employee", (resultSet) -> {
            var e = new Employee();
            e.setId(resultSet.getInt("id"));
            e.setFirstName(resultSet.getString("first_name"));
            e.setEmail(resultSet.getString("email"));
            e.setAvatar(URI.create(resultSet.getString("avatar")));
            return e;
        });
        Assertions.assertEquals(Employee.class, l.get(0).getClass());
        Assertions.assertEquals(1000, l.size());
    }

    @Test
    void testQueryForObject() {
        var l = template.queryForObject("select * from employee where id = 2", resultSet -> {
            var e = new Employee();
            e.setId(resultSet.getInt("id"));
            e.setFirstName(resultSet.getString("first_name"));
            e.setEmail(resultSet.getString("email"));
            e.setAvatar(URI.create(resultSet.getString("avatar")));
            return e;
        });
        Assertions.assertEquals(Employee.class, l.getClass());
        Assertions.assertEquals(l.id, 2);
    }

    @Test
    void testUpdateBatch() {
        var rows = template.updateBatch("update employee set first_name=? where id=?", preparedStatement -> {
            preparedStatement.setString(1, "Gary");
            preparedStatement.setInt(2, 1);
            preparedStatement.addBatch();

            preparedStatement.setString(1, "Wolfram");
            preparedStatement.setInt(2, 2);
            preparedStatement.addBatch();
        });
        Assertions.assertEquals(2, rows.length);
    }

    @Test
    void testExecute() {
        var b = template.execute("select * from employee");
        Assertions.assertTrue(b);
        b = template.execute("invalid DDL SQL");
        Assertions.assertFalse(b);
    }

    private static class Employee {
        private int id;
        private String firstName;
        private String email;
        private URI avatar;

        public Employee() {
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public URI getAvatar() {
            return avatar;
        }

        public void setAvatar(URI avatar) {
            this.avatar = avatar;
        }

        @Override
        public String toString() {
            return "Employee{" +
                    "id=" + id +
                    ", firstName='" + firstName + '\'' +
                    ", email='" + email + '\'' +
                    ", avatar=" + avatar +
                    '}';
        }
    }

}
