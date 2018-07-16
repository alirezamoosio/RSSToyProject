package ir.sahab.rsstoyproject.news;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import java.sql.*;
import java.util.ArrayList;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class NewsDaoImp implements NewsDao {
    private static NewsDaoImp instance;
    private Connection databaseConnector;

    private NewsDaoImp() {
    }

    public NewsDaoImp(String user, String password) {
        try {
            databaseConnector = DriverManager.getConnection("jdbc:mysql://localhost/RSSDatabase?useUnicode=yes&characterEncoding=UTF-8", user, password);
//            databaseStatement = databaseConnector.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static NewsDaoImp getInstance(String user, String password) {
        if (instance == null) {
            instance = new NewsDaoImp(user, password);
        }
        return instance;
    }

    public static NewsDaoImp getInstance() {
        if (instance == null) {
            instance = new NewsDaoImp();
        }
        return instance;
    }

    @Override
    public List<News> getNews() {
        return null;
    }

    @Override
    public boolean addNews(News news) {
        try {
            PreparedStatement databaseStatement = databaseConnector.prepareStatement("insert into News(title, date, link, content, site) values(?, ?, ?, ?, ?);");
            databaseStatement.setString(1, news.getTitle());
            databaseStatement.setTimestamp(2, new java.sql.Timestamp(news.getDate().getTime()));
            databaseStatement.setString(3, news.getLink());
            databaseStatement.setString(4, news.getContent());
//            databaseStatement.setString(5, news.getSite());
            databaseStatement.executeUpdate();
        } catch (MySQLIntegrityConstraintViolationException e) {
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }


    @Override
    public ArrayList<String> search(String field, String text) {
        try {
            ArrayList<String> result = new ArrayList<>();
            PreparedStatement databaseStatement = databaseConnector.prepareStatement("select title from News where ? like %?%;");
            databaseStatement.setString(1, field);
            databaseStatement.setString(2, text);
            ResultSet queryResult = databaseStatement.executeQuery();
            while (queryResult.next()) {
                result.add(queryResult.getString("title"));
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<String> getLatestNews(String siteName) {
        ArrayList<String> titles = new ArrayList<>();
        try {
            PreparedStatement databaseStatement = databaseConnector.prepareStatement("select title from News where site = ? order by date desc limit 10;");
            databaseStatement.setString(1, siteName);
            ResultSet resultSet = databaseStatement.executeQuery();
            while (resultSet.next()) {
                System.out.println(resultSet.getString("title"));
                titles.add(resultSet.getString("title"));
            }
        } catch (MySQLIntegrityConstraintViolationException e) {
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return titles;
    }

    @Override
    public ArrayList<String> getNewsFromADay(String siteName, String date, int length) {
        ArrayList<String> titles = new ArrayList<>();
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat standardFormat = new SimpleDateFormat("yyyy-MM-dd");
            cal.setTime(standardFormat.parse(date));
            cal.add(Calendar.DATE, length);
            String lastDay = standardFormat.format(cal.getTime());
            PreparedStatement databaseStatement = databaseConnector.prepareStatement("select title from News where site = ? and date > ? and date < ?;");
            databaseStatement.setString(1, siteName);
            databaseStatement.setString(2, date);
            databaseStatement.setString(3, lastDay);
            ResultSet resultSet = databaseStatement.executeQuery();
            while (resultSet.next()) {
                titles.add(resultSet.getString("title"));
            }
        } catch (MySQLIntegrityConstraintViolationException e) {
            return null;
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        return titles;
    }
}
