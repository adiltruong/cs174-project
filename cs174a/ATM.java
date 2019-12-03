// package cs174a;
// import java.sql.DatabaseMetaData;
// import java.sql.ResultSet;
// import java.sql.SQLException;
// import java.sql.Statement;
// import java.util.Properties;
// import oracle.jdbc.pool.OracleDataSource;
// //import sun.jvm.hotspot.debugger.posix.elf.ELFSectionHeader;
// import oracle.jdbc.OracleConnection;
// import java.io.BufferedReader;
// import java.io.FileReader;
// import java.io.IOException;
// import java.util.*;

// public class ATM {

//     private String currentUser;
//     private OracleConnection _connection;  
//     private String currentDate;

//     public ATM(){
//         this.currentDate = getDate();
//         this._connection = new OracleConnection();
//     }

//     public boolean login(String name, String pin){
//         try{
//             Statement stmt = _connection.createStatement();
//             ResultSet rs = stmt.executeQuery("SELECT * FROM Customer WHERE name = '"+name +"' AND pin = '"+pin+"')");
//             if (rs.next()) {
//                 currentUser = rs.getString("taxID");
//                 return true;
//             }
//         }catch(Exception e){
//             System.out.println("big oof: "+e);
//         }
//         return false;
//     }

// }

