package com.uberlogik.demo.data;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper  implements ResultSetMapper<User>
{
    public User map(int index, ResultSet r, StatementContext ctx) throws SQLException
    {
        return new User(r.getInt("user_id"), r.getString("username"), r.getString("email"),
                r.getString("pwd"), r.getString("pwd_hash"));
    }
}
