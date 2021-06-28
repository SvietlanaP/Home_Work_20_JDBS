import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DbUserStorage {

    private Connection connection;

    public DbUserStorage(Connection connection) {
        this.connection = connection;
    }

    public void save(User user){
        try {

            List<Address> userAddress = user.getAddress();
            List<Telephone> userPhone = user.getTelephone();

            connection.setAutoCommit(false);

            PreparedStatement preparedUser = connection.prepareStatement("insert into users values (default, ?, ?, ?) returning id");
            preparedUser.setString(1, user.getName());
            preparedUser.setString(2, user.getUsername());
            preparedUser.setString(3, user.getPassword());
            preparedUser.execute();

            ResultSet resultSet1 = preparedUser.executeQuery();
            resultSet1.next();
            int userId = resultSet1.getInt(1);

            for (Address address : userAddress){
                addAddressById(userId, address.getStreet());
            }

            for (Telephone telephone : userPhone){
               addPhoneById(userId, telephone.getNumber());
            }

            connection.commit();
        } catch (SQLException throwables) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            throwables.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    private Optional<List<Address>> addAddressById(int id, String street){
        try {
            List<Address> addressList = new ArrayList<>();

            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement("insert into addresses values (default, ?) returning id");
            preparedStatement.setString(1, street);

            ResultSet resultSet1 = preparedStatement.executeQuery();
            resultSet1.next();
            int addressId = resultSet1.getInt(1);

            PreparedStatement preparedStatement1 = connection.prepareStatement("insert into user_addresses values (?, ?)");
            preparedStatement1.setInt(1, id);
            preparedStatement1.setInt(2, addressId);

            addressList.add(new Address(addressId, street));
            connection.commit();

            return Optional.of(addressList);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return Optional.empty();
    }

    private Optional<List<Telephone>> addPhoneById(int id, String number){
        try {
            List<Telephone> telephoneList = new ArrayList<>();

            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement("insert into telephone values (default, ?) returning id");
            preparedStatement.setString(1, number);

            ResultSet resultSet1 = preparedStatement.executeQuery();
            resultSet1.next();
            int telephoneId = resultSet1.getInt(1);

            PreparedStatement preparedStatement1 = connection.prepareStatement("insert into user_telephone values (?, ?)");
            preparedStatement1.setInt(1, id);
            preparedStatement1.setInt(2, telephoneId);

            telephoneList.add(new Telephone(telephoneId, number));
            connection.commit();

            return Optional.of(telephoneList);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return Optional.empty();
    }



    public Optional<List<User>> getByUsername(String username) {
        try {
            List<User> userList = new ArrayList<>();

            PreparedStatement preparedStatement = connection.prepareStatement("select * from users where username = ?");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                int userId = resultSet.getInt(1);
                String name = resultSet.getString(2);
                String username2 = resultSet.getString(3);
                String password = resultSet.getString(4);

                userList.add(new User(userId, name, username2, password, getUserAddressById(userId), getUserPhoneById(userId)));
            }
            return Optional.of(userList);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<List<User>> getAllByName(String name) {
        try {
            List<User> userList = new ArrayList<>();

            PreparedStatement preparedStatement = connection.prepareStatement("select * from users where name = ?");
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int userId = resultSet.getInt(1);
                String name1 = resultSet.getString(2);
                String username = resultSet.getString(3);
                String password = resultSet.getString(4);

                userList.add(new User(userId, name1, username, password, getUserAddressById(userId), getUserPhoneById(userId)));
            }
            return Optional.of(userList);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<List<User>> getAllbyStreet(String street) {
        try {
            List<User> userList = new ArrayList<>();

            PreparedStatement preparedStatement = connection.prepareStatement("select * from users u \n" +
                                                                                    "    join user_addresses ua on u.id = ua.user_id \n" +
                                                                                    "    join addresses a on a.id = ua.address_id \n" +
                                                                                    "where street = ?");
            preparedStatement.setString(1, street);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                int userId = resultSet.getInt(1);
                String name = resultSet.getString(2);
                String username = resultSet.getString(3);
                String password = resultSet.getString(4);

                userList.add(new User(userId, name, username, password, getUserAddressById(userId), getUserPhoneById(userId)));
            }

            return Optional.of(userList);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Optional.empty();
    }


    public Optional<List<User>> getAll() {
        try {
            List<User> userList = new ArrayList<>();

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from users");

            while (resultSet.next()) {
                int userId = resultSet.getInt(1);
                String name = resultSet.getString(2);
                String username = resultSet.getString(3);
                String password = resultSet.getString(4);

                userList.add(new User(userId, name, username, password, getUserAddressById(userId), getUserPhoneById(userId)));
            }

            return Optional.of(userList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private List<Address> getUserAddressById(int id){
        List<Address> userAddress = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from addresses a join user_addresses ua on a.id = ua.address_id WHERE user_id = ? ");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                int idAddress = resultSet.getInt(1);
                String street = resultSet.getString(2);
                userAddress.add(new Address(idAddress, street));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return userAddress;
    }

    private List<Telephone> getUserPhoneById(int id){
        List<Telephone> userPhone = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from telephone t join user_telephone ut on t.id = ut.telephone_id WHERE user_id = ?");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                int idPhone = resultSet.getInt(1);
                String number = resultSet.getString(2);
                userPhone.add(new Telephone(idPhone, number));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return userPhone;
    }

    public void deleteById(int id){
        try {
            connection.setAutoCommit(false);

            PreparedStatement userStatement = connection.prepareStatement("delete from users where id = ?");
            userStatement.setInt(1, id);
            userStatement.execute();

            PreparedStatement addressIdSrarement = connection.prepareStatement("select a.id from addresses a join user_addresses ua on a.id = ua.address_id where ua.user_id = ?");
            addressIdSrarement.setInt(1, id);
            ResultSet resultSet = addressIdSrarement.executeQuery();
            while (resultSet.next()){
                int addressId = resultSet.getInt(1);
                PreparedStatement preparedStatement2 = connection.prepareStatement("delete from addresses where id = ?");
                preparedStatement2.setInt(1, addressId);
                preparedStatement2.execute();
            }

            PreparedStatement userAddressStatement = connection.prepareStatement("delete from user_addresses where user_id = ?");
            userAddressStatement.setInt(1, id);
            userAddressStatement.execute();

            PreparedStatement phoneIdSrarement = connection.prepareStatement("select t.id from telephone t join user_telephone ut on t.id = ut.telephone_id where ut.user_id = ?");
            phoneIdSrarement.setInt(1, id);
            ResultSet resultSet2 = phoneIdSrarement.executeQuery();
            while (resultSet2.next()){
                int phoneId = resultSet2.getInt(1);
                PreparedStatement preparedStatement3 = connection.prepareStatement("delete from telephone where id = ?");
                preparedStatement3.setInt(1, phoneId);
                preparedStatement3.execute();
            }

            PreparedStatement userPhoneStatement = connection.prepareStatement("delete from user_telephone where user_id = ?");
            userPhoneStatement.setInt(1, id);
            userPhoneStatement.execute();

            connection.commit();
        } catch (SQLException throwables) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            throwables.printStackTrace();
        }finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public boolean exist(User user){
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from users u where u.name = ? and u.username = ? and u.password = ?");
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.setString(3, user.getPassword());

            preparedStatement.execute();

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                String name = resultSet.getString(1);
                String username = resultSet.getString(2);
                String password = resultSet.getString(3);

                if (name.equals(user.getName()) && username.equals(user.getUsername()) && password.equals(user.getPassword())){
                   return true;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public boolean existById(int id){
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from users where id = ?");
            preparedStatement.setInt(1, id);

            preparedStatement.execute();

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                int idUser = resultSet.getInt(1);

                if (idUser == id){
                    return true;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return false;
    }

    public void updatePasswordById(int id, String password){
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("update users set password = ? where id = ?");
            preparedStatement.setString(1, password);
            preparedStatement.setInt(2, id);
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void updateNameById(int id, String name){
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("update users set name = ? where id = ?");
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, id);
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
