package lk.ijse.pos.controller;

import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.pos.AppInitializer;
import lk.ijse.pos.bo.BOFactory;
import lk.ijse.pos.bo.custom.ItemBO;
import lk.ijse.pos.dto.ItemDTO;
import lk.ijse.pos.entity.Item;
import lk.ijse.pos.view.tblmodel.ItemTM;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ManageItemFormController<JFXTextField> implements Initializable {



    @FXML
    private JFXTextField txtItemCode;
    @FXML
    private JFXTextField txtDescription;
    @FXML
    private JFXTextField txtUnitPrice;
    @FXML
    private JFXTextField txtQty;
    @FXML
    private AnchorPane root;
    @FXML
    private TableView<ItemTM> tblItems;
    private boolean addNew = true;

    private ItemBO itemBO = (ItemBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.ITEM);

    private void loadAllItems() {

        try {
            ArrayList<ItemDTO> allItems = itemBO.getAllItems();
            /*create a ItemTM type list*/
            ArrayList<ItemTM> allItemsForTable = new ArrayList<>();
            for (ItemDTO i : allItems) {
                allItemsForTable.add(new ItemTM(i.getCode(), i.getDescription(), i.getUnitPrice(), i.getQtyOnHand()));
            }
            ObservableList<ItemTM> olItems = FXCollections.observableArrayList(allItemsForTable);
            tblItems.setItems(olItems);
        } catch (Exception ex) {
            Logger.getLogger(ManageItemFormController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        tblItems.getColumns().get(0).setStyle("-fx-alignment: center");
        tblItems.getColumns().get(2).setStyle("-fx-alignment: center-right");
        tblItems.getColumns().get(3).setStyle("-fx-alignment: center-right");

        tblItems.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("code"));
        tblItems.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblItems.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblItems.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));

        loadAllItems();

        tblItems.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ItemTM>() {
            @Override
            public void changed(ObservableValue<? extends ItemTM> observable, ItemTM oldValue, ItemTM newValue) {

                if (newValue == null) {
                    addNew = true;
                    clearTextFields();
                    return;
                }

                txtItemCode.setText(newValue.getCode());
                txtDescription.setText(newValue.getDescription());
                txtUnitPrice.setText(newValue.getUnitPrice().toPlainString());
                txtQty.setText(newValue.getQtyOnHand() + "");
                addNew = false;
            }
        });
    }

    private void clearTextFields() {
        txtItemCode.setText("");
        txtDescription.setText("");
        txtUnitPrice.setText("");
        txtQty.setText("");
    }

    @FXML
    private void navigateToHome(MouseEvent event) {
        AppInitializer.navigateToHome(root, (Stage) root.getScene().getWindow());
    }

    @FXML
    private void btnAddNewItem_OnAction(ActionEvent event) {
        tblItems.getSelectionModel().clearSelection();
        txtItemCode.requestFocus();
        addNew = true;
    }

    @FXML
    private void btnSave_OnAction(ActionEvent event) {
        if (addNew) {
            try {
                ItemDTO item = new ItemDTO(txtItemCode.getText(), txtDescription.getText(), new BigDecimal(txtUnitPrice.getText()), Integer.parseInt(txtQty.getText()));
                boolean b = itemBO.addItem(item);
                if (b) {
                    loadAllItems();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to add the item", ButtonType.OK).show();
                }
            } catch (Exception ex) {
                Logger.getLogger(ManageItemFormController.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            try {
                ItemDTO item = new ItemDTO(txtItemCode.getText(), txtDescription.getText(), new BigDecimal(txtUnitPrice.getText()), Integer.parseInt(txtQty.getText()));
                boolean b = itemBO.updateItem(item);
                if (b) {
                    loadAllItems();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to update the item", ButtonType.OK).show();
                }
            } catch (Exception ex) {
                Logger.getLogger(ManageItemFormController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


    }

    @FXML
    private void btnDelete_OnAction(ActionEvent event) {

        if (tblItems.getSelectionModel().getSelectedIndex() == -1) return;
        String code = tblItems.getSelectionModel().getSelectedItem().getCode();

        try {
            boolean b = itemBO.deleteItem(code);
            if (b) {
                loadAllItems();
            } else {
                new Alert(Alert.AlertType.ERROR, "Unable to delete the customer", ButtonType.OK).show();
            }
        } catch (Exception ex) {
            Logger.getLogger(ManageItemFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
