package java.com.erikmolssons.template;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface PreparedStatementBuilder {
    void build(PreparedStatement preparedStatement) throws SQLException;
}
