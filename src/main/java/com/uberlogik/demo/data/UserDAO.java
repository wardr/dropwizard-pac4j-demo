package com.uberlogik.demo.data;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.util.List;

public interface UserDAO
{
    @SqlQuery("select * from user where username = :name")
    List<User> findByUsername(@Bind("name") String name);

    void close();
}
