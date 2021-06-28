import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "711Pental");
            DbUserStorage dbUserStorage = new DbUserStorage(connection);

            List<Address> addressUser = new ArrayList<>();
            List<Telephone> telephoneUser = new ArrayList<>();
            addressUser.add(new Address(0, "hsjdh"));
            telephoneUser.add(new Telephone(0, "20000"));
            User user = new User(0,"test7", "test7", "test7", addressUser, telephoneUser);

            dbUserStorage.save(user);
            //dbUserStorage.updatePasswordById(1, "passwordNew");
            //dbUserStorage.updateNameById(3,"qwert");

            //не видит существующего пользователя
            //System.out.println(dbUserStorage.exist(user));

            //System.out.println(dbUserStorage.existById(2));

            //System.out.println(dbUserStorage.getAll().toString());

            //System.out.println(dbUserStorage.getAllByName("Test2").toString());

            //System.out.println(dbUserStorage.getAllbyStreet("romy").toString());

            //System.out.println(dbUserStorage.getByUsername("test21").toString());

            //dbUserStorage.deleteById(1);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


//        Connection connection = null;
//        try {
//            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "711Pental");
//            Statement statement = connection.createStatement();
//
//            //добавить данные в базу данных
//            //statement.execute("insert into users values (default , 'Test1', 'test1','password1')");
//
//            //изменение поля name
//            //statement.execute("update users set name = 'newName' where id = 2");
//
//            //удалить запись
//            //statement.execute("delete  from users  where id = 2");
//
//            //достать все данные из базы
////            ResultSet resultSet = statement.executeQuery("select * from users ");
////            List<User> users = new ArrayList<>();
////            while (resultSet.next()) {
////                int id = resultSet.getInt(1);
////                String name = resultSet.getString(2);
////                String username = resultSet.getString(3);
////                String password = resultSet.getString(4);
////                User user = new User(id, name, username, password);
////                users.add(user);
////            }
////                System.out.println(users);
//
//            // достать данные по параметру
////            ResultSet resultSet = statement.executeQuery("select * from users where id = 3");
////            resultSet.next();
////            int id = resultSet.getInt(1);
////            String name = resultSet.getString(2);
////            String username = resultSet.getString(3);
////            String password = resultSet.getString(4);
////            User user = new User(id, name, username, password);
////            System.out.println(user);
//
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }


    }

}
