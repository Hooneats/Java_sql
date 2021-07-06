package sample;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller implements Initializable {

    @FXML
    public TextArea textArea;
    @FXML
    public TextField textField;
    @FXML
    public ListView listView;
    @FXML
    public TableView tableView;
    @FXML
    public Button button1;
    @FXML
    public Button button2;
    @FXML
    public Button button3;
    @FXML
    public Label label1;
    @FXML
    public Label label2;
    @FXML
    public ImageView imageView;

    String url = "jdbc:oracle:thin:@localhost:1521:JAVA";
    String usr = "scott";
    String pwd = "tiger";

    Connection con;
    Statement stm;
    ResultSet rs;

    String tName;

    int liNum=0;

    public void setLabel1(){
        label1.setText("USER_NAME : "+usr.toUpperCase());
    }

    public void onClickBut1(ActionEvent actionEvent) {
        try {
            textField.setText("");
            String msg = textArea.getText();
            int indexDB = msg.indexOf(" ");
            String msgDB = msg.substring(0,indexDB);
            switch (msgDB){
                case "select": DQL(msg); break;
                case "SELECT": DQL(msg); break;
                case "conn" : connLogin(msg); break;
                case "CONN" : connLogin(msg); break;
                default: nomalExecute(msg);
            }
        }catch (Exception e){
            System.out.println("확인용");
            textField.setText("");
            textField.setText("Fail");
        }

    }

    public void connLogin(String msg) {
        System.out.println("dsadasfasdaasdasdasfas");
        int s = msg.indexOf(" ");
        int e = msg.indexOf("/");
        usr = msg.substring(s + 1, e);
        pwd = msg.substring(e + 1);
        System.out.println(usr+"///"+pwd);
        try {
            con = DriverManager.getConnection(url, usr, pwd);
            System.out.println("Succeed");
            textField.setText("Succeed");
            textArea.setText("");
            stm = con.createStatement();
            tableList();
            setLabel1();
        } catch (SQLException throwables) {
            textField.setText("");
            textField.setText("Fail");
            throwables.printStackTrace();
        }
    }

    public void nomalExecute(String msg){
        try {
            stm.execute(msg);

            System.out.println("Succeed");
            textField.setText("Succeed");
            textArea.setText("");
            tableList();
        } catch (SQLException throwables) {
            textField.setText("");
            textField.setText("Fail");
            throwables.printStackTrace();
        }
    }

    public void onClickBut2(ActionEvent actionEvent) {
        textField.setText("");
        textArea.setText("");
        tableList();
    }

    //Collumn 불러오기
    public void DQL(String msg){
        ObservableList<ObservableList> data;

        data = FXCollections.observableArrayList();
        try {

            rs = stm.executeQuery(msg);
            ResultSetMetaData rsM = rs.getMetaData();
            int colCount = rsM.getColumnCount();


            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });
                tableView.getColumns().addAll(col);
//                System.out.println("Column [" + i + "] ");
            }
            while(rs.next()){
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
                    //Iterate Column
                    Optional<String> value = Optional.ofNullable(rs.getString(i));
                    String nameValue = String.valueOf(value);
                    if (value.isPresent()) {
                        row.add(String.valueOf(value).substring(nameValue.indexOf("[") + 1, nameValue.lastIndexOf("]")));
                    } else {
                        row.add(String.valueOf(value));
                    }
                }
//                System.out.println("Row [1] added "+row );
                data.add(row);
            }
            tableView.setItems(data);

//            System.out.println("Succeed");
            textField.setText("Succeed");
            textArea.setText("");

        } catch (SQLException throwables) {
            textField.setText("");
            textField.setText("Fail");
            throwables.printStackTrace();
        }
    }

    public void keyEn(KeyEvent keyEvent) {
        if (keyEvent.getCode() == keyEvent.getCode().ENTER) {
            button1.fire();
        }
    }

    public void clearView(ActionEvent actionEvent){
        String selectedTable = (String) listView.getSelectionModel().getSelectedItem();
        System.out.println(selectedTable);
        String sql = "truncate table " + selectedTable;
        nomalExecute(sql);
        textField.setText("");
        tableList();

    }

    public void onMousse(MouseEvent event){

        textField.setText("");
        while (true){
            if(!tableView.getColumns().isEmpty()){
                tableView.getColumns().remove(0);
            }else break;
        }
        tName = listView.getSelectionModel().getSelectedItem().toString();
//        System.out.println(tName); --확인용
        String sql = "select * from "+tName;

        DQL(sql);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection(url,usr,pwd);
            stm = con.createStatement();
//            System.out.println("연결 성공!!!");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        tableList();
        setLabel1();
        textArea.setPromptText("Prease write the SQL one sentence at a time. "+
                    "\nPlease write 'select all column.'");
        textField.setPromptText("Succeed / Fail");

    }

    public void tableList(){
        try {
            while(true){
                if(!listView.getItems().isEmpty()){
                    listView.getItems().remove(0);
                } else break;
            }
            rs = stm.executeQuery("select tname from tab");
            while (rs.next()) {
                liNum++;
                String tableName = rs.getString(1);
                listView.getItems().add(tableName);
            }
        } catch (SQLException throwables) {
            textField.setText("");
            textField.setText("Fail");
            throwables.printStackTrace();
        }
    }

}
