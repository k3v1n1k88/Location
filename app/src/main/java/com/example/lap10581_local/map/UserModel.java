package com.example.lap10581_local.map;

import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserModel {
    private JDBCController jdbcController = new JDBCController();
    private Connection connection;

    public UserModel() {
        connection = jdbcController.ConnnectionData(); // Tạo kết nối tới database
    }

    public User getUserfromID(String iD) throws SQLException {
        User user = null;
        Statement statement = connection.createStatement();// Tạo đối tượng Statement.
        String sql = "select * from user_table "
                +"where ID = "+iD;
        Log.d("Sql instruction",sql);
        // Thực thi câu lệnh SQL trả về đối tượng ResultSet. // Mọi kết quả trả về sẽ được lưu trong ResultSet
        ResultSet rs = statement.executeQuery(sql);
        rs.next();

        // Đọc dữ liệu từ ResultSet
        String userID = rs.getString("ID");
        String userUserName = rs.getString("Name");
        String userPassword  = rs.getString("Password");
        String userName = rs.getString("Username");
        String userPhone = rs.getString("Phone");

        user = new User(userID,userUserName,userPassword,userName,userPhone);

        connection.close();// Đóng kết nối
        return user;
    }

    public User getUserFromUsername(String username) throws SQLException {
        User user = null;
        Statement statement = connection.createStatement();// Tạo đối tượng Statement.
        String sql = "select * from user_table "
                +"where username = '"+username+"'";
        Log.d("Sql instruction",sql);
        // Thực thi câu lệnh SQL trả về đối tượng ResultSet. // Mọi kết quả trả về sẽ được lưu trong ResultSet
        ResultSet rs = statement.executeQuery(sql);
        rs.next();

        // Đọc dữ liệu từ ResultSet
        String userID = rs.getString("ID");
        String userUserName = rs.getString("Name");
        String userPassword  = rs.getString("Password");
        String userName = rs.getString("Username");
        String userPhone = rs.getString("Phone");

        user = new User(userID,userUserName,userPassword,userName,userPhone);

        connection.close();// Đóng kết nối
        return user;
    }

    public boolean Insert(User objUser) throws SQLException {
        Statement statement = connection.createStatement();// Tạo đối tượng Statement.
        String sql = "insert into user(ID,Username,Password,Name,Phone)+"
                    + " values("
                    + objUser.getiD()+ ","
                    + objUser.getUserName()+ ","
                    + objUser.getPassWord()+ ","
                    + objUser.getName()+ ","
                    + objUser.getPhone()+","
                    + ")";
        if (statement.executeUpdate(sql) > 0) { // Dùng lệnh executeUpdate cho các lệnh CRUD
            connection.close();
            return true;
        } else {
            connection.close();
            return false;
        }
    }

    public boolean Update(User objUser) throws SQLException {
        Statement statement = connection.createStatement();// Tạo đối tượng Statement.
        String sql = "Update User set"
                    + "Name = " + objUser.getName() + ","
                    + "Username = " + objUser.getUserName() + ","
                    + "Password = " + objUser.getPassWord() + ","
                    + "Phone = " + objUser.getPhone()
                    + "where ID = "
                    + objUser.getiD();
        if (statement.executeUpdate(sql) > 0) {
            connection.close();
            return true;
        } else
            connection.close();
        return false;
    }

    public boolean Delete(User objUser) throws SQLException {
        Statement statement = connection.createStatement();// Tạo đối tượng Statement.
        String sql = "delete from User where ID = " + objUser.getiD();
        if (statement.executeUpdate(sql) > 0){
            connection.close();
            return true;
        }

        else
            connection.close();
        return false;
    }
}
