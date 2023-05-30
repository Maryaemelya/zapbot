import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class TestConnection {
    public static final  String user_name = "root";
    public static final  String password = "Maryemelya112358";
    public static final  String url = "jdbc:mysql://localhost:3306/mysql";
    public static Statement statement;
    public  static Connection connection;

    static {
        try{
            connection = DriverManager.getConnection(url,user_name,password);
        }catch (SQLException trowables){
            trowables.printStackTrace();
            throw new RuntimeException();
        }
    }
    static {
        try{
            statement = connection.createStatement();
        }catch (SQLException trowables){
            trowables.printStackTrace();
            throw new RuntimeException();
        }
    }

    //если какой-то код уже реализован в mysql, то здесь его нужно закомментировать, так как он будет выполнятся при каждом обращении. Может выдать ошибку, если выполнить код в mysql будет нельзя
    public boolean setKlient(String phone,String Kname, String birthday) throws ClassNotFoundException,SQLException, IOException {
     //   Class.forName("com.mysql.cj.jdbc.Driver");
//        statement.executeUpdate("CREATE TABLE `klient` (\n" +
//                "\t`phoneklient` varchar(255) NOT NULL,\n" +
//                "\t`nameklient` varchar(255) NOT NULL,\n" +
//                "\t`birthdayklient` DATE NOT NULL,\n" +
//                "\tPRIMARY KEY (`phoneklient`));");
        //cod tablici
        //statement.executeUpdate("TRUNCATE TABLE klient;");
        ResultSet idsush = statement.executeQuery("SELECT COUNT(*) FROM klient WHERE phoneklient = '" + phone + "';");
        if (idsush.next() && idsush.getInt(1) == 0) {
            statement.executeUpdate("insert into klient(phoneklient,nameklient,birthdayklient) value ('" + phone + "','" + Kname + "','" + birthday + "');");
            //добавление данных в таблицу
            ResultSet resultSet = statement.executeQuery("select * from klient");
            return true;
        }
        else {return false;}
    }
public ArrayList<String[]> checkSpecData(String spec) throws SQLException {
    ResultSet resultSet = statement.executeQuery("SELECT * FROM doctor WHERE jobdoctor = '" +spec +"'");
    //получение данных из таблицы
    ArrayList<String[]> docs = new ArrayList<String[]>();
    while (resultSet.next()){
        String[] docmas = new String[2];
        docmas[0]= resultSet.getString(1);
        docmas[1]= resultSet.getString(2);
        docs.add(docmas);
    }
    return docs;
    }

    public ArrayList<String[]> checkDay(String iddoc) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT DISTINCT datedoctor FROM zap \n" +
                "WHERE phonedoctor = '" + iddoc + "';");
        //получение данных из таблицы
        ArrayList<String[]> docs = new ArrayList<String[]>();
        while (resultSet.next()){
            String[] docmas = new String[2];
            docmas[0]=resultSet.getString(1);
            docs.add(docmas);
        }
        return docs;
    }

    public ArrayList<String[]> checkTime(String iddoc,String datedoctor ) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM zap WHERE phonedoctor = '" + iddoc + "' AND datedoctor = '" + datedoctor + "' AND phoneklient IS NULL;");
        //получение данных из таблицы
        ArrayList<String[]> docs = new ArrayList<String[]>();
        while (resultSet.next()){
            String[] docmas = new String[3];
            docmas[0]=resultSet.getString(4);
            docmas[1]=resultSet.getString(1);
            docmas[2]=resultSet.getString(5);
            docs.add(docmas);
        }
        return docs;
    }

    public ArrayList<String[]> raspisanieKlient(String idKlient) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT clinic.nameclinic, CONCAT(clinic.cityclinic, ', ', clinic.addressclinic,  ',  каб. ', zap.cabinetclinic) AS adress, doctor.namedoctor, zap.datedoctor, zap.timedoctor, zap.phonedoctor\n" +
                "FROM zap\n" +
                "JOIN clinic ON zap.idclinic = clinic.idclinic\n" +
                "JOIN doctor ON zap.phonedoctor = doctor.phonedoctor\n" +
                "Where zap.phoneklient = '"+idKlient+"'\n" +
                "GROUP BY zap.datedoctor, zap.timedoctor, clinic.nameclinic, clinic.cityclinic, clinic.addressclinic, zap.cabinetclinic, doctor.namedoctor, zap.phoneklient, zap.phonedoctor \n ORDER BY zap.datedoctor asc, zap.timedoctor asc;\n");
        //получение данных из таблицы
        ArrayList<String[]> docs = new ArrayList<String[]>();
        int r = 0;
        while (resultSet.next()){
            String[] docmas = new String[7];
            r +=1;
            docmas[0]=String.valueOf(r);
            docmas[1]=resultSet.getString(1);
            docmas[2]=resultSet.getString(2);
            docmas[3]=resultSet.getString(3);
            docmas[4]=resultSet.getString(4);
            docmas[5]=resultSet.getString(5);
            docmas[6]=resultSet.getString(6);
            docs.add(docmas);
        }
        return docs;
    }

    public String[] zapisKlient(String iddoc,String datedoc, String timedoc,String idklient) throws SQLException {
        statement.executeUpdate("UPDATE zap SET phoneklient ='" + idklient + "' WHERE phonedoctor = '" + iddoc + "' AND datedoctor = '" + datedoc + "' AND timedoctor = '" + timedoc +"';");
        ResultSet resultSet = statement.executeQuery("select *from zap WHERE phoneklient ='" + idklient + "' and phonedoctor = '" + iddoc + "' AND datedoctor = '" + datedoc + "' AND timedoctor = '" + timedoc +"';");
        //получение данных из таблицы
        String[] docs = new String[6];
        while (resultSet.next()){
            docs[0]=resultSet.getString(1);
            docs[1]=resultSet.getString(2);
            docs[2]=resultSet.getString(3);
            docs[3]=resultSet.getString(4);
            docs[4]=resultSet.getString(5);
            docs[5]=resultSet.getString(6);
        }
        return docs;
    }

    public void deleteZapis(String iddoc,String datedoc, String timedoc,String idklient) throws SQLException {
        statement.executeUpdate("UPDATE zap SET phoneklient = NULL WHERE phoneklient = '" + idklient + "' AND phonedoctor = '" + iddoc + "' AND datedoctor = '" + datedoc +"' AND timedoctor = '" + timedoc + "';");
    }


        public String[] nameClinic(String idclin) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM clinic WHERE idclinic = " + idclin + ";");
        //получение данных из таблицы
        String[] docs = new String[4];
        while (resultSet.next()){
            docs[0]=resultSet.getString(1);
            docs[1]=resultSet.getString(2);
            docs[2]=resultSet.getString(3);
            docs[3]=resultSet.getString(4);
        }
        return docs;
    }
}
