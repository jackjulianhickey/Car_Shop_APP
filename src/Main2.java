import java.awt.image.BufferedImage;
import java.sql.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Main2 extends Application {
	ArrayList<String> carName = new ArrayList<String>();
	ArrayList<String> filePath = new ArrayList<String>();
	ArrayList<String> carId = new ArrayList<String>();
	ArrayList<String> cartName = new ArrayList<String>();
	ArrayList<String> addDesc = new ArrayList<String>();
	ArrayList<Double> addPrice = new ArrayList<Double>();
	
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost/SHOP";
	
	   static final String USER = "sqluser";
	   static final String PASS = "sqluserpw";
	
	Connection conn = null;
	Statement stmt = null;
	

	Product addProd;
	ScrollPane main = new ScrollPane();
	Button seeProducts, addProduct, seeCart;
	GridPane mainGrid = new GridPane();
	GridPane cartGrid = new GridPane();
	GridPane addProductGrid = new GridPane();
	TextField desc;
	TextField imagePath, price;
	ImageView showImage;
	Image image, carPic = null;
	File file, txtFile;

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws FileNotFoundException, IOException, SQLException {		
		primaryStage.setTitle("JavaFx E-Commerce Application");
		Group root = new Group();
		Scene scene = new Scene(root, 900,800);

		populateArrays();
		main.setPrefSize(900, 850);
		main.setTranslateX(150);
		main.setStyle("-fx-background-color: white;");

		AnchorPane sidePanel = new AnchorPane();
		sidePanel.setMinSize(150, 850);
		sidePanel.setStyle("-fx-background-color: gray;");

		seeProducts = new Button("Products");
		seeProducts.setPrefWidth(100);
		seeProducts.setTranslateX(25);
		seeProducts.setTranslateY(40);

		seeProducts.setOnAction(e -> {
			showProducts();
		});

		seeCart = new Button("Cart");
		seeCart.setPrefWidth(100);
		seeCart.setTranslateX(25);
		seeCart.setTranslateY(70);
		seeCart.setOnAction(e -> {
			showCart();
		});

		Button addProduct = new Button("New Product");
		addProduct.setPrefWidth(100);
		addProduct.setTranslateX(25);
		addProduct.setTranslateY(100);
		addProduct.setOnAction(e -> {
			newProduct();
		});

		setupMainGrid();

		sidePanel.getChildren().addAll(seeProducts, seeCart, addProduct);
		root.getChildren().addAll(main, sidePanel);
		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void setupMainGrid(){
		mainGrid = new GridPane();
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPrefWidth(150);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPrefWidth(350);
		ColumnConstraints col3 = new ColumnConstraints();
		col3.setPrefWidth(100);
		mainGrid.setPrefWidth(650);
		mainGrid.setMinHeight(600);
		mainGrid.getColumnConstraints().addAll(col1,col2,col3);
		mainGrid.setVgap(10);
		PopulateProducts();
		main.setContent(mainGrid);
	}

	private void newProduct() {
		// TODO Auto-generated method stub
		Label nameLabel = new Label("Car Name:");
		GridPane.setConstraints(nameLabel, 0, 1);

		TextField name = new TextField("Car Name");
		GridPane.setConstraints(name, 1, 1);
		name.setOnMouseClicked(new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent arg0) {

				name.clear();
			}

		});

		Label descLabel = new Label("Description:");
		GridPane.setConstraints(descLabel, 0, 3);

		desc = new TextField("Description");
		GridPane.setConstraints(desc, 1, 3);
		desc.setOnMouseClicked(new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent arg0) {

				desc.clear();
			}

		});
		
		Label imageLabel = new Label("Image Path:");
		GridPane.setConstraints(imageLabel, 0, 7);
		
		imagePath = new TextField("Image Path");
		GridPane.setConstraints(imagePath, 1, 7);
		imagePath.setOnMouseClicked(new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent arg0) {

				imagePath.clear();
			}
		});
		
		Label priceLabel = new Label("Price:");
		GridPane.setConstraints(priceLabel, 0, 5);
		
		price = new TextField("price");
		GridPane.setConstraints(price, 1, 5);
		price.setOnMouseClicked(new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent arg0) {

				price.clear();
			}
		});
		
		Button add = new Button("OK");
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(add);
		GridPane grid = new GridPane();
		grid.add(hbBtn, 1, 9);

		showImage = new ImageView();

		Button imageAdd = new Button("Select Image");
		hbBtn.getChildren().add(imageAdd);
		imageAdd.setOnAction(e -> {
			filePicker();

		});

		/*TableView table = new TableView<>();
		table.getColumns().addAll(nameColumn, descColumn);*/

		ArrayList<Product> list = new ArrayList<Product>();

		add.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e ) {
				
				try {
					Class.forName("com.mysql.jdbc.Driver");

				      //STEP 3: Open a connection
				      System.out.println("Connecting to a selected database...");
				      conn = DriverManager.getConnection(DB_URL,USER, PASS);
				      System.out.println("Connected database successfully...");
					
					PreparedStatement preparedStatement = conn.prepareStatement("insert into  SHOP.PRODUCTS values (default, ?, ?, ?, ?)");
					
					preparedStatement.setString(1, name.getText());
					preparedStatement.setString(2, desc.getText());
					try{
					preparedStatement.setDouble(3, Double.parseDouble(price.getText()));
					}
					catch (Exception e1){
						alertBox("Please enter a valid price using digits!");
					}
					preparedStatement.setString(4, imagePath.getText());
					preparedStatement.executeUpdate();
					
				} catch (SQLException e1) {
					System.out.println("Did not add to Database");
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				finally{
				      //finally block used to close resources
				      try{
				         if(stmt!=null)
				            conn.close();
				      }catch(SQLException se){
				      }// do nothing
				      try{
				         if(conn!=null)
				            conn.close();
				      }catch(SQLException se){
				         se.printStackTrace();
				      }//end finally try
				   }//end try
				   System.out.println("Goodbye!");

				carName.add(name.getText());
				filePath.add(imagePath.getText());
				name.clear();
				desc.clear();
				imagePath.clear();
				price.clear();
			}
		});

		VBox vbox = new VBox();
		vbox.getChildren().addAll(showImage);
		HBox hbVBox = new HBox(10);
		hbVBox.setAlignment(Pos.BOTTOM_RIGHT);
		hbVBox.getChildren().add(vbox);
		grid.add( hbVBox, 1, 6); 


		grid.getChildren().addAll(nameLabel, name, descLabel, desc, price, priceLabel, imagePath, imageLabel );

		main.setContent(grid);


	}

	private void filePicker(){

		FileChooser chooser = new FileChooser();
		chooser.setTitle("Open File");
		file = chooser.showOpenDialog(new Stage());
		imagePath.setText(file.getAbsolutePath());



	}

	public void addToCart(String id){
		int num = Integer.parseInt(id);
		System.out.println(carName.get(num) + " has been added to cart");
		cartName.add(carName.get(num));
		alertBox(carName.get(num) + " has been added to cart");
	}

	public void showProducts() {
		setupMainGrid();
	}

	public void showCart(){
		cartGrid = new GridPane();
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPrefWidth(150);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPrefWidth(350);
		ColumnConstraints col3 = new ColumnConstraints();
		col3.setPrefWidth(100);

		cartGrid.setPrefWidth(650);
		cartGrid.setMinHeight(600);
		cartGrid.getColumnConstraints().addAll(col1,col2,col3);
		cartGrid.setVgap(10);

		populateCart();
		main.setContent(cartGrid);
	}

	public void populateCart(){

		String image = null, carStr = null;
		Image carImage = null;
		for(int i = 0; i < cartName.size(); i++){
			carStr = cartName.get(i);
			for(int j = 0; j < carName.size(); j++){
				if(cartName.get(i).equals(carName.get(j))){
					File sourceimage = new File(filePath.get(j));
					carImage = new Image(sourceimage.toURI().toString());
				}
			}
			Button remove = new Button("Remove");
			remove.setId(carStr);
			String id = remove.getId();
			remove.setOnAction(e -> {
				removeCart(id);
			});
			Label carN = new Label(carStr);
			carN.setStyle("-fx-font-size: 1.3em;");
			ImageView car = new ImageView(carImage);
			car.setFitWidth(300);
			car.setTranslateX(25);
			car.setFitHeight(150);
			//if(!cartGrid.getRowConstraints().contains(carStr)){
			cartGrid.addRow(i+1, carN, car, remove);
			//	}
		}
	}

	public void removeCart(String id) {
		for(int i = 0; i < cartName.size(); i++){
			if(cartName.get(i).equals(id)){
				cartName.remove(i);
				break;
			}

		}

		showCart();
	}

	public void alertBox(String text){
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Alert Box");
		alert.setHeaderText(null);
		alert.setContentText(text);

		alert.showAndWait();
	}

	public void populateArrays() throws FileNotFoundException, IOException, SQLException{
		/*try (BufferedReader br = new BufferedReader(new FileReader("resources/file.txt"))) {
			String line;
			while ((line = br.readLine()) != null) {
				//if(line.charAt(3) == ' '){
				carName.add(line);
				line = br.readLine();
				//}
				//else{
				filePath.add(line);
				//}
			}
		}*/
		
		int count = 0;
		conn = DriverManager.getConnection(DB_URL,USER, PASS);
		stmt = conn.createStatement();
		ResultSet res = stmt.executeQuery("SELECT * FROM products");
		while(res.next()){
			//addProd = new Product(res.getInt("productID"), res.getString("name"), res.getString("description"), res.getDouble("price"), res.getString("image"));
			//prod.add (new Product(res.getInt("productID"), res.getString("name"), res.getString("description"), res.getDouble("price"), res.getString("image")));
			carName.add(res.getString("name"));
			filePath.add(res.getString("image"));
			addDesc.add(res.getString("description"));
			addPrice.add(res.getDouble("price"));
			
		}

	}

	public void PopulateProducts(){
		
		
		for(int i = 0; i < carName.size(); i++){
			Button addToCart = new Button("Buy " + addPrice.get(i) + " ");
			addToCart.setMinSize(200, 50);
			addToCart.prefWidth(200);
			addToCart.setTranslateX(35);
			addToCart.setId(""+i);
			addToCart.setOnAction(e -> {
				addToCart(addToCart.getId());
			});
			File sourceimage = new File(filePath.get(i));
			carPic = new Image(sourceimage.toURI().toString());
			Label carN = new Label(carName.get(i));
			carN.setPrefWidth(100);
			carN.setAlignment(Pos.CENTER);
			carN.setTranslateX(25);
			carN.setStyle("-fx-font-size: 1.3em;");
			ImageView car = new ImageView(carPic);
			car.setFitWidth(300);
			car.setTranslateX(25);
			car.setFitHeight(150);
			Label carDesc = new Label(addDesc.get(i));
			carDesc.setPrefWidth(100);
			carDesc.setAlignment(Pos.CENTER);
			carDesc.setTranslateX(25);
			carDesc.setStyle("-fx-font-size: 1.3em;");
			mainGrid.addRow(i+1, carN, car,carDesc,addToCart);
		}
	}
}
