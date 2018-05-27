/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vendor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
abstract public class vendor_model {
    protected String table;
    protected Connection conn;
    
    public vendor_model() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String connectionURL = "jdbc:mysql://" + CONFIG.DATABASE_HOST + ":" + CONFIG.DATABASE_PORT + "/" + CONFIG.DATABASE_DB;
            conn = (Connection) DriverManager.getConnection(connectionURL, CONFIG.DATABASE_USER, CONFIG.DATABASE_PASS);
        } catch (Exception ex) {
            Logger.getLogger(vendor_model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public ResultSet excute(String sql) throws SQLException {
        PreparedStatement pstm = conn.prepareStatement(sql);
        return pstm.executeQuery();
    }
    
    public ResultSet get(String fields, String lConditions, String[] rConditions) throws SQLException {
        ResultSet rs = null;
        String sql = "SELECT " + fields + " FROM `" + table + "`";
        if (rConditions.length > 0 && lConditions.equals("") == false) { sql += " WHERE " + lConditions; }
        PreparedStatement pstm = conn.prepareStatement(sql);

        for (int i=1; i<=rConditions.length; i++) { pstm.setString(i, rConditions[i-1]); }
        rs = (ResultSet) pstm.executeQuery();
        
        return rs;
    }
    
    public int insert(String[] fields, String[] values) throws SQLException {
        if (fields.length != values.length) { return -1; }
        
        String sql = "INSERT INTO " + table + " (";
        String val = "VALUES (";
        
        for (String s : fields) { sql += s + ","; val += "?,"; }
        sql = sql.substring(0, sql.length() - 1);
        val = val.substring(0, val.length() - 1);
        sql = sql + ") " + val + ")";
        
        PreparedStatement pstm = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        for (int i=1; i<=values.length; i++) { pstm.setString(i, values[i-1]); }
        return pstm.executeUpdate();
    }
    
    public int update(int id, String[] fields, String[] values) throws SQLException {
        if (fields.length != values.length || fields.length == 0) { return -1; }
        String sql = "UPDATE " + table + " SET ";
        for (int i=0; i<fields.length; i++) { sql += fields[i] + "=?,"; }
        sql = sql.substring(0, sql.length() - 1);
        sql += " WHERE id_" + table + "=?";
        System.out.println(sql);
        
        PreparedStatement pstm = conn.prepareStatement(sql); int i = 1;
        for (i=1; i<=values.length; i++) { pstm.setString(i, values[i-1]); }
        pstm.setString(i, id + "");
        System.out.println(pstm);
        return pstm.executeUpdate();
    }
}
