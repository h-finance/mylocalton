package org.ton.ui.controllers;

import com.jfoenix.controls.*;
import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.ton.actions.MyLocalTon;
import org.ton.db.entities.BlockEntity;
import org.ton.db.entities.TxEntity;
import org.ton.db.entities.WalletEntity;
import org.ton.enums.LiteClientEnum;
import org.ton.executors.blockchainexplorer.BlockchainExplorer;
import org.ton.executors.liteclient.LiteClient;
import org.ton.executors.liteclient.LiteClientParser;
import org.ton.executors.liteclient.api.*;
import org.ton.executors.liteclient.api.block.Transaction;
import org.ton.executors.liteclient.api.config.Validator;
import org.ton.main.App;
import org.ton.parameters.ValidationParam;
import org.ton.settings.*;
import org.ton.utils.Utils;
import org.ton.wallet.WalletVersion;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static com.sun.javafx.PlatformUtil.*;
import static java.util.Objects.*;
import static org.ton.actions.MyLocalTon.MAX_ROWS_IN_GUI;
import static org.ton.main.App.fxmlLoader;
import static org.ton.main.App.mainController;

@Slf4j
public class MainController implements Initializable {

    public static final String LIGHT_BLUE = "#dbedff";
    public static final String ORANGE = "orange";
    public static final int YEAR_1971 = 34131600;
    public static final long ONE_BLN = 1000000000L;
    @FXML
    public StackPane superWindow;

    @FXML
    public BorderPane mainWindow;

    @FXML
    public JFXTabPane mainMenuTabs;

    @FXML
    public JFXTabPane settingTabs;

    @FXML
    public Label currentBlockNum;

    @FXML
    public Label liteClientInfo;

    @FXML
    public Label shardsNum;

    @FXML
    public ImageView scrollBtnImageView;

    @FXML
    public HBox topbar;

    @FXML
    public JFXListView<Node> blockslistviewid;

    @FXML
    public JFXListView<Node> transactionsvboxid;

    @FXML
    public JFXListView<Node> accountsvboxid;

    @FXML
    public TextField electedFor;

    @FXML
    public TextField initialBalance;

    @FXML
    public TextField globalId;

    @FXML
    public TextField electionStartBefore;

    @FXML
    public TextField electionEndBefore;

    @FXML
    public TextField stakesFrozenFor;

    @FXML
    public TextField gasPrice;

    @FXML
    public TextField cellPrice;

    @FXML
    public TextField nodeStateTtl1;

    @FXML
    public TextField nodeBlockTtl1;

    @FXML
    public TextField nodeArchiveTtl1;

    @FXML
    public TextField nodeKeyProofTtl1;

    @FXML
    public TextField nodeSyncBefore1;

    @FXML
    public Tab settingsTab;

    @FXML
    public Tab accountsTab;

    @FXML
    public Tab transactionsTab;

    @FXML
    public JFXButton myLocalTonDbDirBtn;

    @FXML
    public Tab logsTab;

    @FXML
    public Tab validationTab;

    @FXML
    public JFXTabPane validationTabs;

    @FXML
    public Label nodeStatus2;

    @FXML
    public Label nodeStatus3;

    @FXML
    public Label nodePublicPort1;

    @FXML
    public Label nodeConsolePort1;

    @FXML
    public Label liteServerPort1;

    @FXML
    public Label nodeStatus1;

    @FXML
    public Label totalParticipants;

    @FXML
    public Label totalValidators;

    @FXML
    public JFXCheckBox enableBlockchainExplorer;

    @FXML
    public Label enableBlockchainExplorerLabel;

    @FXML
    public Tab explorerTab;

    @FXML
    public WebView webView;

    @FXML
    public Label validator1WalletBalance;

    @FXML
    public Label validator1WalletAddress;

    @FXML
    public Label validator1AdnlAddress;

    @FXML
    public Label blockchainLaunched;

    @FXML
    public Label startCycle;

    @FXML
    public Label endCycle;

    @FXML
    public Label startElections;

    @FXML
    public Label endElections;

    @FXML
    public Label nextElections;

    @FXML
    public Label minterAddr;

    @FXML
    public Label configAddr;

    @FXML
    public Label electorAddr;

    @FXML
    public Label validationPeriod;

    @FXML
    public Label electionPeriod;

    @FXML
    public Label holdPeriod;

    @FXML
    public Label minimumStake;

    @FXML
    public Label maximumStake;

    @FXML
    public Label validator1PubKeyHex;

    @FXML
    public ProgressBar validationCountDown;

    @FXML
    public Label minterBalance;

    @FXML
    public Label configBalance;

    @FXML
    public Label electorBalance;

    @FXML
    public Label legendHoldStake;

    @FXML
    public Label legendValidation;

    @FXML
    public Label legendElections;

    @FXML
    public Label legendPause;

    @FXML
    public Label stakeHoldRange3;

    @FXML
    public Label validationRange3;

    @FXML
    public Label pauseRange3;

    @FXML
    public Label electionsRange3;

    @FXML
    public Label stakeHoldRange2;

    @FXML
    public Label validationRange2;

    @FXML
    public Label pauseRange2;

    @FXML
    public Label electionsRange2;

    @FXML
    public Label stakeHoldRange1;

    @FXML
    public Label validationRange1;

    @FXML
    public Label pauseRange1;

    @FXML
    public Label electionsRange1;

    @FXML
    public Pane electionsChartPane;

    @FXML
    public Separator timeLine;

    @FXML
    public Label validator1PubKeyInteger;

    @FXML
    public ProgressBar progressValidationUpdate;

    @FXML
    public Label validator1AdnlAddressNext;

    @FXML
    public Label validator1PubKeyHexNext;

    @FXML
    public Label validator1PubKeyIntegerNext;

    @FXML
    public Label validator1totalCollected;

    @FXML
    public Label validator1LastCollected;

    @FXML
    public Label validator1TotalRewardsPure;

    @FXML
    public Label validator1LastRewardPure;

    @FXML
    public Label validator1AvgPureReward;

    @FXML
    public Label participatedInElections1;

    @FXML
    public JFXButton addValidatorBtn;

    @FXML
    public Tab genesisnode1;

    @FXML
    public Tab validator2tab;

    @FXML
    public Tab validator3tab;

    @FXML
    public Tab validator4tab;

    @FXML
    public Tab validator5tab;

    @FXML
    public Tab validator6tab;

    @FXML
    public Tab validator7tab;

    @FXML
    public Label nodePublicPort2;

    @FXML
    public Label nodeConsolePort2;

    @FXML
    public Label liteServerPort2;

    @FXML
    public Label validator2AdnlAddress;

    @FXML
    public Label validator2PubKeyHex;

    @FXML
    public Label validator2PubKeyInteger;

    @FXML
    public Label validator2AdnlAddressNext;

    @FXML
    public Label validator2PubKeyIntegerNext;

    @FXML
    public Label validator2PubKeyHexNext;

    @FXML
    public Label validator2WalletAddress;

    @FXML
    public Label validator2WalletBalance;

    @FXML
    public Label validator2totalCollected;

    @FXML
    public Label validator2LastCollected;

    @FXML
    public Label validator2TotalRewardsPure;

    @FXML
    public Label validator2LastRewardPure;

    @FXML
    public Label validator2AvgPureReward;

    @FXML
    public Label participatedInElections2;

    @FXML
    public JFXButton deleteValidatorBtn2;

    @FXML
    public Label nodePublicPort3;

    @FXML
    public Label nodeConsolePort3;

    @FXML
    public Label liteServerPort3;

    @FXML
    public Label validator3AdnlAddress;

    @FXML
    public Label validator3PubKeyHex;

    @FXML
    public Label validator3PubKeyInteger;

    @FXML
    public Label validator3AdnlAddressNext;

    @FXML
    public Label validator3PubKeyHexNext;

    @FXML
    public Label validator3PubKeyIntegerNext;

    @FXML
    public Label validator3WalletAddress;

    @FXML
    public Label validator3WalletBalance;

    @FXML
    public Label validator3totalCollected;

    @FXML
    public Label validator3TotalRewardsPure;

    @FXML
    public Label validator3LastCollected;

    @FXML
    public Label validator3LastRewardPure;

    @FXML
    public Label validator3AvgPureReward;

    @FXML
    public Label participatedInElections3;

    @FXML
    public JFXButton deleteValidatorBtn3;

    @FXML
    public Label nodeStatus4;

    @FXML
    public Label nodePublicPort4;

    @FXML
    public Label nodeConsolePort4;

    @FXML
    public Label liteServerPort4;

    @FXML
    public Label validator4AdnlAddress;

    @FXML
    public Label validator4AdnlAddressNext;

    @FXML
    public Label validator4PubKeyHexNext;

    @FXML
    public Label validator4PubKeyIntegerNext;

    @FXML
    public Label validator4WalletAddress;

    @FXML
    public Label validator4WalletBalance;

    @FXML
    public Label validator4totalCollected;

    @FXML
    public Label validator4LastCollected;

    @FXML
    public Label validator4TotalRewardsPure;

    @FXML
    public Label validator4LastRewardPure;

    @FXML
    public Label validator4AvgPureReward;

    @FXML
    public Label participatedInElections4;

    @FXML
    public JFXButton deleteValidatorBtn4;

    @FXML
    public Label nodeStatus5;

    @FXML
    public Label nodePublicPort5;

    @FXML
    public Label nodeConsolePort5;

    @FXML
    public Label liteServerPort5;

    @FXML
    public Label validator5AdnlAddress;

    @FXML
    public Label validator5PubKeyHex;

    @FXML
    public Label validator5PubKeyInteger;

    @FXML
    public Label validator5AdnlAddressNext;

    @FXML
    public Label validator5PubKeyHexNext;

    @FXML
    public Label validator5PubKeyIntegerNext;

    @FXML
    public Label validator5WalletAddress;

    @FXML
    public Label validator5WalletBalance;

    @FXML
    public Label validator5totalCollected;

    @FXML
    public Label validator5LastCollected;

    @FXML
    public Label validator5TotalRewardsPure;

    @FXML
    public Label validator5LastRewardPure;

    @FXML
    public Label validator5AvgPureReward;

    @FXML
    public Label participatedInElections5;

    @FXML
    public JFXButton deleteValidatorBtn5;

    @FXML
    public Label nodeStatus6;

    @FXML
    public Label nodePublicPort6;

    @FXML
    public Label nodeConsolePort6;

    @FXML
    public Label liteServerPort6;

    @FXML
    public Label validator6AdnlAddress;

    @FXML
    public Label validator6PubKeyHex;

    @FXML
    public Label validator6PubKeyInteger;

    @FXML
    public Label validator6AdnlAddressNext;

    @FXML
    public Label validator6PubKeyHexNext;

    @FXML
    public Label validator6PubKeyIntegerNext;

    @FXML
    public Label validator6WalletAddress;

    @FXML
    public Label validator6WalletBalance;

    @FXML
    public Label validator6totalCollected;

    @FXML
    public Label validator6LastCollected;

    @FXML
    public Label validator6TotalRewardsPure;

    @FXML
    public Label validator6LastRewardPure;

    @FXML
    public Label validator6AvgPureReward;

    @FXML
    public Label participatedInElections6;

    @FXML
    public JFXButton deleteValidatorBtn6;

    @FXML
    public Label nodeStatus7;

    @FXML
    public Label nodePublicPort7;

    @FXML
    public Label nodeConsolePort7;

    @FXML
    public Label liteServerPort7;

    @FXML
    public Label validator7AdnlAddress;

    @FXML
    public Label validator7PubKeyHex;

    @FXML
    public Label validator7PubKeyInteger;

    @FXML
    public Label validator7AdnlAddressNext;

    @FXML
    public Label validator7PubKeyHexNext;

    @FXML
    public Label validator7PubKeyIntegerNext;

    @FXML
    public Label validator7WalletAddress;

    @FXML
    public Label validator7WalletBalance;

    @FXML
    public Label validator7totalCollected;

    @FXML
    public Label validator7LastCollected;

    @FXML
    public Label validator7TotalRewardsPure;

    @FXML
    public Label validator7LastRewardPure;

    @FXML
    public Label validator7AvgPureReward;

    @FXML
    public Label participatedInElections7;

    @FXML
    public JFXButton deleteValidatorBtn7;

    @FXML
    public Label validator4PubKeyHex;

    @FXML
    public Label validator4PubKeyInteger;

    @FXML
    public Tab settingsLogsValidator1Tab;

    @FXML
    public JFXTabPane subLogsTabs;

    @FXML
    public JFXTextField validatorLogDir2;

    @FXML
    public JFXComboBox<String> tonLogLevel2;

    @FXML
    public JFXTextField validatorLogDir3;

    @FXML
    public JFXComboBox<String> tonLogLevel3;

    @FXML
    public JFXTextField validatorLogDir4;

    @FXML
    public JFXComboBox<String> tonLogLevel4;

    @FXML
    public JFXTextField validatorLogDir5;

    @FXML
    public JFXComboBox<String> tonLogLevel5;

    @FXML
    public JFXTextField validatorLogDir6;

    @FXML
    public JFXComboBox<String> tonLogLevel6;

    @FXML
    public JFXTextField validatorLogDir7;

    @FXML
    public JFXComboBox<String> tonLogLevel7;

    @FXML
    public JFXTextField nodeStateTtl2;

    @FXML
    public JFXTextField nodeBlockTtl2;

    @FXML
    public JFXTextField nodeArchiveTtl2;

    @FXML
    public JFXTextField nodeKeyProofTtl2;

    @FXML
    public JFXTextField nodeSyncBefore2;

    @FXML
    public JFXTextField configNodePublicPort2;

    @FXML
    public JFXTextField configNodeConsolePort2;

    @FXML
    public JFXTextField configLiteServerPort2;

    @FXML
    public JFXTextField validatorWalletDeposit2;

    @FXML
    public JFXTextField validatorDefaultStake2;

    @FXML
    public JFXTextField validatorWalletDeposit1;

    @FXML
    public JFXTextField validatorDefaultStake1;

    @FXML
    public JFXTextField nodeSyncBefore3;

    @FXML
    public JFXTextField nodeKeyProofTtl3;

    @FXML
    public JFXTextField nodeArchiveTtl3;

    @FXML
    public JFXTextField nodeBlockTtl3;

    @FXML
    public JFXTextField nodeStateTtl3;

    @FXML
    public JFXTextField validatorDefaultStake3;

    @FXML
    public JFXTextField validatorWalletDeposit3;

    @FXML
    public JFXTextField configLiteServerPort3;

    @FXML
    public JFXTextField configNodePublicPort3;

    @FXML
    public JFXTextField configNodeConsolePort3;

    @FXML
    public JFXTextField configNodeConsolePort4;

    @FXML
    public JFXTextField configNodePublicPort4;

    @FXML
    public JFXTextField configLiteServerPort4;

    @FXML
    public JFXTextField validatorWalletDeposit4;

    @FXML
    public JFXTextField validatorDefaultStake4;

    @FXML
    public JFXTextField nodeStateTtl4;

    @FXML
    public JFXTextField nodeBlockTtl4;

    @FXML
    public JFXTextField nodeArchiveTtl4;

    @FXML
    public JFXTextField nodeKeyProofTtl4;

    @FXML
    public JFXTextField nodeSyncBefore4;

    @FXML
    public JFXTextField configNodeConsolePort5;

    @FXML
    public JFXTextField configNodePublicPort5;

    @FXML
    public JFXTextField configLiteServerPort5;

    @FXML
    public JFXTextField validatorWalletDeposit5;

    @FXML
    public JFXTextField validatorDefaultStake5;

    @FXML
    public JFXTextField nodeStateTtl5;

    @FXML
    public JFXTextField nodeBlockTtl5;

    @FXML
    public JFXTextField nodeArchiveTtl5;

    @FXML
    public JFXTextField nodeKeyProofTtl5;

    @FXML
    public JFXTextField nodeSyncBefore5;

    @FXML
    public JFXTextField configNodeConsolePort6;

    @FXML
    public JFXTextField configNodePublicPort6;

    @FXML
    public JFXTextField configLiteServerPort6;

    @FXML
    public JFXTextField validatorWalletDeposit6;

    @FXML
    public JFXTextField validatorDefaultStake6;

    @FXML
    public JFXTextField nodeStateTtl6;

    @FXML
    public JFXTextField nodeBlockTtl6;

    @FXML
    public JFXTextField nodeArchiveTtl6;

    @FXML
    public JFXTextField nodeKeyProofTtl6;

    @FXML
    public JFXTextField nodeSyncBefore6;

    @FXML
    public JFXTextField configNodeConsolePort7;

    @FXML
    public JFXTextField configNodePublicPort7;

    @FXML
    public JFXTextField configLiteServerPort7;

    @FXML
    public JFXTextField validatorWalletDeposit7;

    @FXML
    public JFXTextField validatorDefaultStake7;

    @FXML
    public JFXTextField nodeStateTtl7;

    @FXML
    public JFXTextField nodeBlockTtl7;

    @FXML
    public JFXTextField nodeArchiveTtl7;

    @FXML
    public JFXTextField nodeKeyProofTtl7;

    @FXML
    public JFXTextField nodeSyncBefore7;

    @FXML
    public Label tonDonationAddress;

    @FXML
    JFXCheckBox shardStateCheckbox;

    @FXML
    JFXCheckBox showMsgBodyCheckBox;

    @FXML
    public Tab searchTab;

    @FXML
    Label searchTabText;

    @FXML
    JFXTextField searchField;

    @FXML
    public Tab foundBlocks;

    @FXML
    public Tab foundAccounts;

    @FXML
    public Tab foundTxs;

    @FXML
    public JFXTabPane foundTabs;

    @FXML
    public JFXListView<Node> foundBlockslistviewid;

    @FXML
    public JFXListView<Node> foundTxsvboxid;

    @FXML
    public JFXListView<Node> foundAccountsvboxid;

    @FXML
    public Tab blocksTab;

    @FXML
    TextField configNodePublicPort1;

    @FXML
    TextField configNodeConsolePort1;

    @FXML
    TextField configLiteServerPort1;

    @FXML
    TextField configDhtServerPort1;

    @FXML
    ImageView aboutLogo;

    @FXML
    JFXTextField gasPriceMc;

    @FXML
    JFXTextField cellPriceMc;

    @FXML
    JFXTextField maxFactor;

    @FXML
    JFXTextField minTotalStake;

    @FXML
    JFXTextField maxStake;

    @FXML
    JFXTextField minStake;

    @FXML
    JFXComboBox<String> walletVersion;

    @FXML
    Label statusBar;

    @FXML
    private JFXButton scrollBtn;

    @FXML
    private JFXSlider walletsNumber;

    @FXML
    private TextField coinsPerWallet;

    @FXML
    private TextField validatorLogDir1;

    @FXML
    private TextField dhtLogDir1;

    @FXML
    private TextField minValidators;

    @FXML
    private TextField maxValidators;

    @FXML
    private TextField maxMainValidators;

    @FXML
    private TextField myLocalTonLog;

    @FXML
    public JFXCheckBox tickTockCheckBox;

    @FXML
    public JFXCheckBox mainConfigTxCheckBox;

    @FXML
    public JFXCheckBox inOutMsgsCheckBox;

    @FXML
    public Label dbSizeId;

    @FXML
    public ComboBox<String> myLogLevel;

    @FXML
    public ComboBox<String> tonLogLevel;

    private MyLocalTonSettings settings;

    JFXDialog sendDialog;
    JFXDialog yesNoDialog;

    public void showSendDialog(String srcAddr) throws IOException {

        Parent parent = new FXMLLoader(App.class.getClassLoader().getResource("org/ton/main/dialogsend.fxml")).load();

        ((Label) parent.lookup("#hiddenWalletAddr")).setText(srcAddr);

        JFXDialogLayout content = new JFXDialogLayout();
        content.setBody(parent);

        sendDialog = new JFXDialog(superWindow, content, JFXDialog.DialogTransition.CENTER);
        sendDialog.setOnKeyPressed(keyEvent -> {
                    if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                        sendDialog.close();
                    }
                }
        );
        sendDialog.setOnDialogOpened(jfxDialogEvent -> parent.lookup("#destAddr").requestFocus());
        sendDialog.show();
    }

    public void showInfoMsg(String msg, double durationSeconds) {
        Platform.runLater(() -> {
            statusBar.setStyle("-fx-text-fill: black; -fx-background-color: dbedff");
            Rectangle rect = new Rectangle();
            rect.setFill(Color.valueOf(LIGHT_BLUE));
            statusBar.setBackground(new Background(new BackgroundFill(rect.getFill(), CornerRadii.EMPTY, Insets.EMPTY)));
            statusBar.setText(msg);
            animateBackgroundColor(statusBar, Color.valueOf(LIGHT_BLUE), Color.valueOf(LIGHT_BLUE), (int) (durationSeconds * 1000));
            animateFontColor(statusBar, Color.BLACK, Color.valueOf(LIGHT_BLUE), (int) (durationSeconds * 1000));
        });
    }

    public void showSuccessMsg(String msg, double durationSeconds) {
        Platform.runLater(() -> {
            statusBar.setStyle("-fx-text-fill: white; -fx-background-color: green");
            Rectangle rect = new Rectangle();
            rect.setFill(Color.GREEN);
            statusBar.setBackground(new Background(new BackgroundFill(rect.getFill(), CornerRadii.EMPTY, Insets.EMPTY)));
            statusBar.setText(msg);

            animateBackgroundColor(statusBar, Color.GREEN, Color.valueOf(LIGHT_BLUE), (int) (durationSeconds * 1000));
            animateFontColor(statusBar, Color.WHITE, Color.valueOf(LIGHT_BLUE), (int) (durationSeconds * 1000));
        });
    }

    public void showErrorMsg(String msg, double durationSeconds) {
        Platform.runLater(() -> {
            statusBar.setStyle("-fx-text-fill: black; -fx-background-color: lightcoral");
            Rectangle rect = new Rectangle();
            rect.setFill(Color.valueOf(LIGHT_BLUE));
            statusBar.setBackground(new Background(new BackgroundFill(rect.getFill(), CornerRadii.EMPTY, Insets.EMPTY)));
            statusBar.setText(msg);
            animateBackgroundColor(statusBar, Color.valueOf("lightcoral"), Color.valueOf(LIGHT_BLUE), (int) (durationSeconds * 1000));
            animateFontColor(statusBar, Color.BLACK, Color.valueOf(LIGHT_BLUE), (int) (durationSeconds * 1000));
        });
    }

    public void showWarningMsg(String msg, double durationSeconds) {
        Platform.runLater(() -> {
            statusBar.setStyle("-fx-text-fill: black; -fx-background-color: orange");
            Rectangle rect = new Rectangle();
            rect.setFill(Color.valueOf(ORANGE));
            statusBar.setBackground(new Background(new BackgroundFill(rect.getFill(), CornerRadii.EMPTY, Insets.EMPTY)));
            statusBar.setText(msg);
        });
    }

    public void showShutdownMsg(String msg, double durationSeconds) {
        Platform.runLater(() -> {
            statusBar.setStyle("-fx-text-fill: black; -fx-background-color: orange");
            Rectangle rect = new Rectangle();
            rect.setFill(Color.valueOf(ORANGE));
            statusBar.setBackground(new Background(new BackgroundFill(rect.getFill(), CornerRadii.EMPTY, Insets.EMPTY)));
            statusBar.setText(msg);

            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            service.schedule(() -> {
                log.info("final closing");
                saveSettings();
                Platform.exit(); // closes main form

                if (Utils.doShutdown()) {
                    log.info("system exit 0");
                    System.exit(0);
                }
            }, 3, TimeUnit.SECONDS);
        });
    }

    public static void animateBackgroundColor(Control control, Color fromColor, Color toColor, int duration) {

        Rectangle rect = new Rectangle();
        rect.setFill(fromColor);

        Rectangle rectFont = new Rectangle();
        rectFont.setFill(Color.BLACK);

        FillTransition tr = new FillTransition();
        tr.setShape(rect);
        tr.setDuration(Duration.millis(1000));
        tr.setFromValue(fromColor);
        tr.setToValue(toColor);

        tr.setInterpolator(new Interpolator() {
            @Override
            protected double curve(double t) {
                control.setBackground(new Background(new BackgroundFill(rect.getFill(), CornerRadii.EMPTY, Insets.EMPTY)));
                return t;
            }
        });
        tr.setDelay(Duration.millis(duration));
        tr.play();
    }

    public static void animateFontColor(Control control, Color fromColor, Color toColor, int duration) {

        Rectangle rect = new Rectangle();
        rect.setFill(fromColor);

        FillTransition tr = new FillTransition();
        tr.setShape(rect);
        tr.setDuration(Duration.millis(1000));
        tr.setFromValue(fromColor);
        tr.setToValue(toColor);

        tr.setInterpolator(new Interpolator() {
            @Override
            protected double curve(double t) {
                ((Label) control).setTextFill(rect.getFill());
                return t;
            }
        });
        tr.setDelay(Duration.millis(duration));
        tr.play();
    }

    public void shutdown() {
        saveSettings();
    }

    @FXML
    void myLocalTonFileBtnAction() throws IOException {
        log.info("open mylocalton log {}", myLocalTonLog.getText().trim());
        if (isWindows()) {
            Runtime.getRuntime().exec("cmd /c start notepad " + myLocalTonLog.getText());
        } else {
            Runtime.getRuntime().exec("gio open " + myLocalTonLog.getText());
        }
    }

    @FXML
    void dhtLogDirBtnAction1() throws IOException {
        log.debug("open dht dir {}", dhtLogDir1.getText().trim());
        if (isWindows()) {
            Runtime.getRuntime().exec("cmd /c start " + dhtLogDir1.getText());
        } else {
            Runtime.getRuntime().exec("gio open " + dhtLogDir1.getText());
        }
    }

    @FXML
    void valLogDirBtnAction1() throws IOException {
        log.debug("open validator log dir {}", validatorLogDir1.getText().trim());
        if (isWindows()) {
            Runtime.getRuntime().exec("cmd /c start " + validatorLogDir1.getText());
        } else {
            Runtime.getRuntime().exec("gio open " + validatorLogDir1.getText());
        }
    }

    @FXML
    void blocksOnScroll(ScrollEvent event) {

        Node n1 = blockslistviewid.lookup(".scroll-bar");

        if (n1 instanceof ScrollBar) {
            ScrollBar bar = (ScrollBar) n1;

            if (event.getDeltaY() < 0 && bar.getValue() > 0) { // bottom reached
                Platform.runLater(() -> {
                    BorderPane bp = (BorderPane) blockslistviewid.getItems().get(blockslistviewid.getItems().size() - 1);
                    long lastSeqno = Long.parseLong(((Label) ((Node) bp).lookup("#seqno")).getText());
                    long wc = Long.parseLong(((Label) ((Node) bp).lookup("#wc")).getText());

                    long createdAt = Utils.datetimeToTimestamp(((Label) ((Node) bp).lookup("#createdat")).getText());

                    log.debug("bottom reached, seqno {}, time {}, hwm {} ", lastSeqno, Utils.toUtcNoSpace(createdAt), MyLocalTon.getInstance().getBlocksScrollBarHighWaterMark().get());

                    if (lastSeqno == 1L && wc == -1L) {
                        return;
                    }

                    if (blockslistviewid.getItems().size() > MAX_ROWS_IN_GUI) {
                        showWarningMsg("Maximum amount (" + MyLocalTon.getInstance().getBlocksScrollBarHighWaterMark().get() + ") of visible blocks in GUI reached.", 5);
                        return;
                    }

                    List<BlockEntity> blocks = App.dbPool.loadBlocksBefore(createdAt);
                    MyLocalTon.getInstance().getBlocksScrollBarHighWaterMark().addAndGet(blocks.size());

                    ObservableList<Node> blockRows = FXCollections.observableArrayList();

                    for (BlockEntity block : blocks) {
                        try {
                            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("blockrow.fxml"));
                            javafx.scene.Node blockRow = fxmlLoader.load();

                            ResultLastBlock resultLastBlock = ResultLastBlock.builder()
                                    .createdAt(block.getCreatedAt())
                                    .seqno(block.getSeqno())
                                    .rootHash(block.getRoothash())
                                    .fileHash(block.getFilehash())
                                    .wc(block.getWc())
                                    .shard(block.getShard())
                                    .build();

                            MyLocalTon.getInstance().populateBlockRowWithData(resultLastBlock, blockRow, null);

                            if (resultLastBlock.getWc() == -1L) {
                                blockRow.setStyle("-fx-background-color: e9f4ff;");
                            }
                            log.debug("Adding block {} roothash {}", block.getSeqno(), block.getRoothash());

                            blockRows.add(blockRow);

                        } catch (IOException e) {
                            log.error("Error loading blockrow.fxml file, {}", e.getMessage());
                            return;
                        }
                    }

                    log.debug("blockRows.size  {}", blockRows.size());

                    if ((blockRows.isEmpty()) && (lastSeqno < 10)) {
                        log.debug("On start some blocks were skipped, load them now from 1 to {}", lastSeqno - 1);

                        LongStream.range(1, lastSeqno).forEach(i -> { // TODO for loop big integer
                            try {
                                ResultLastBlock block = LiteClientParser.parseBySeqno(LiteClient.getInstance(LiteClientEnum.GLOBAL).executeBySeqno(MyLocalTon.getInstance().getSettings().getGenesisNode(), -1L, "8000000000000000", new BigInteger(String.valueOf(i))));
                                log.debug("Load missing block {}: {}", i, block.getFullBlockSeqno());
                                MyLocalTon.getInstance().insertBlocksAndTransactions(MyLocalTon.getInstance().getSettings().getGenesisNode(), block, false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                    blockslistviewid.getItems().addAll(blockRows);
                });
            }
            if (event.getDeltaY() > 0) { // top reached
                log.debug("top reached");
            }
        }
    }

    @FXML
    void txsOnScroll(ScrollEvent event) {

        log.debug("txsOnScroll: {}", event);

        Node n1 = transactionsvboxid.lookup(".scroll-bar");

        if (n1 instanceof ScrollBar) {
            ScrollBar bar = (ScrollBar) n1;

            if (event.getDeltaY() < 0 && bar.getValue() > 0) { // bottom reached

                Platform.runLater(() -> {

                    BorderPane bp = (BorderPane) transactionsvboxid.getItems().get(transactionsvboxid.getItems().size() - 1);
                    String shortseqno = ((Label) ((Node) bp).lookup("#block")).getText();

                    long createdAt = Utils.datetimeToTimestamp(((Label) ((Node) bp).lookup("#time")).getText());

                    BlockShortSeqno blockShortSeqno = BlockShortSeqno.builder()
                            .wc(Long.valueOf(StringUtils.substringBetween(shortseqno, "(", ",")))
                            .shard(StringUtils.substringBetween(shortseqno, ",", ","))
                            .seqno(new BigInteger(StringUtils.substring(StringUtils.substringAfterLast(shortseqno, ","), 0, -1)))
                            .build();

                    log.debug("bottom reached, seqno {}, hwm {}, createdAt {}, utc {}", blockShortSeqno.getSeqno(), MyLocalTon.getInstance().getTxsScrollBarHighWaterMark().get(), createdAt, Utils.toUtcNoSpace(createdAt));

                    if (blockShortSeqno.getSeqno().compareTo(BigInteger.ONE) == 0) {
                        return;
                    }

                    if (transactionsvboxid.getItems().size() > MAX_ROWS_IN_GUI) {
                        showWarningMsg("Maximum amount (" + MyLocalTon.getInstance().getTxsScrollBarHighWaterMark().get() + ") of visible TXs in GUI reached.", 5);
                        return;
                    }

                    List<TxEntity> txs = App.dbPool.loadTxsBefore(createdAt);

                    MyLocalTon.getInstance().applyTxGuiFilters(txs);

                    MyLocalTon.getInstance().getTxsScrollBarHighWaterMark().addAndGet(txs.size());

                    ObservableList<Node> txRows = FXCollections.observableArrayList();

                    for (TxEntity txEntity : txs) {
                        try {
                            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("txrow.fxml"));
                            javafx.scene.Node txRow = fxmlLoader.load();

                            String shortBlock = String.format("(%d,%s,%d)", txEntity.getWc(), txEntity.getShard(), txEntity.getSeqno());

                            ResultListBlockTransactions resultListBlockTransactions = ResultListBlockTransactions.builder()
                                    .txSeqno(new BigInteger(txEntity.getSeqno().toString()))
                                    .hash(txEntity.getTxHash())
                                    .accountAddress(txEntity.getTx().getAccountAddr())
                                    .lt(txEntity.getTx().getLt())
                                    .build();

                            Transaction txDetails = Transaction.builder()
                                    .accountAddr(txEntity.getTx().getAccountAddr())
                                    .description(txEntity.getTx().getDescription())
                                    .inMsg(txEntity.getTx().getInMsg())
                                    .endStatus(txEntity.getTx().getEndStatus())
                                    .now(txEntity.getTx().getNow())
                                    .totalFees(txEntity.getTx().getTotalFees())
                                    .lt(new BigInteger(txEntity.getTxLt().toString()))
                                    .build();

                            MyLocalTon.getInstance().populateTxRowWithData(shortBlock, resultListBlockTransactions, txDetails, txRow, txEntity);

                            if (txEntity.getTypeTx().equals("Message")) {
                                txRow.setStyle("-fx-background-color: e9f4ff;");
                            }

                            log.debug("adding tx hash {}, addr {}", txEntity.getTxHash(), txEntity.getTx().getAccountAddr());

                            txRows.add(txRow);

                        } catch (IOException e) {
                            log.error("error loading txrow.fxml file, {}", e.getMessage());
                            return;
                        }
                    }
                    log.debug("txRows.size  {}", txRows.size());

                    if ((txRows.isEmpty()) && (blockShortSeqno.getSeqno().compareTo(BigInteger.TEN) < 0)) {
                        log.debug("on start some blocks were skipped and thus some transactions get lost, load them from blocks 1");

                        LongStream.range(1, blockShortSeqno.getSeqno().longValue()).forEach(i -> {
                            try {
                                ResultLastBlock block = LiteClientParser.parseBySeqno(LiteClient.getInstance(LiteClientEnum.GLOBAL).executeBySeqno(MyLocalTon.getInstance().getSettings().getGenesisNode(), -1L, "8000000000000000", new BigInteger(String.valueOf(i))));
                                log.debug("load missing block {}: {}", i, block.getFullBlockSeqno());
                                MyLocalTon.getInstance().insertBlocksAndTransactions(MyLocalTon.getInstance().getSettings().getGenesisNode(), block, false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                    transactionsvboxid.getItems().addAll(txRows);
                });
            }
            if (event.getDeltaY() > 0) { // top reached
                log.debug("top reached");
            }
        }
    }

    @FXML
    void scrollBtnAction() {
        MyLocalTon.getInstance().setAutoScroll(!MyLocalTon.getInstance().getAutoScroll());

        if (Boolean.TRUE.equals(MyLocalTon.getInstance().getAutoScroll())) {
            scrollBtnImageView.setImage(new Image(requireNonNull(getClass().getResourceAsStream("/org/ton/images/scroll.png"))));
        } else {
            scrollBtnImageView.setImage(new Image(requireNonNull(getClass().getResourceAsStream("/org/ton/images/scrolloff.png"))));
        }
        log.debug("auto scroll {}", MyLocalTon.getInstance().getAutoScroll());
    }

    private void showLoading(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        //stage.initStyle(StageStyle.TRANSPARENT);
        //stage.setFill(Color.TRANSPARENT);
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("modal_progress" + ".fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);

        stage.setTitle("My modal window");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((Node) event.getSource()).getScene().getWindow());
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        settings = MyLocalTon.getInstance().getSettings();

        WebEngine browser = webView.getEngine();

        walletsNumber.setOnMouseReleased(event -> {
            log.debug("walletsNumber released, {}", walletsNumber.getValue());
        });

        settingTabs.getSelectionModel().selectedItemProperty().addListener(e -> {
            log.debug("settings tab changed, save settings");
            saveSettings();
        });

        mainMenuTabs.getSelectionModel().selectedItemProperty().addListener(e -> {
            log.debug("main menu changed, save settings");
            saveSettings();
        });

        EventHandler<KeyEvent> onlyDigits = keyEvent -> {
            if (!((TextField) keyEvent.getSource()).getText().matches("[\\d\\.\\-]+")) {
                ((TextField) keyEvent.getSource()).setText(((TextField) keyEvent.getSource()).getText().replaceAll("[^\\d\\.\\-]", ""));
            }
        };

        coinsPerWallet.setOnKeyTyped(onlyDigits);

        configNodePublicPort1.setOnKeyTyped(onlyDigits);
        configNodeConsolePort1.setOnKeyTyped(onlyDigits);
        configLiteServerPort1.setOnKeyTyped(onlyDigits);
        configDhtServerPort1.setOnKeyTyped(onlyDigits);
        validatorWalletDeposit1.setOnKeyTyped(onlyDigits);
        validatorDefaultStake1.setOnKeyTyped(onlyDigits);
        nodeStateTtl1.setOnKeyTyped(onlyDigits);
        nodeBlockTtl1.setOnKeyTyped(onlyDigits);
        nodeArchiveTtl1.setOnKeyTyped(onlyDigits);
        nodeKeyProofTtl1.setOnKeyTyped(onlyDigits);
        nodeSyncBefore1.setOnKeyTyped(onlyDigits);

        configNodePublicPort2.setOnKeyTyped(onlyDigits);
        configNodeConsolePort2.setOnKeyTyped(onlyDigits);
        configLiteServerPort2.setOnKeyTyped(onlyDigits);
        validatorWalletDeposit2.setOnKeyTyped(onlyDigits);
        validatorDefaultStake2.setOnKeyTyped(onlyDigits);
        nodeStateTtl2.setOnKeyTyped(onlyDigits);
        nodeBlockTtl2.setOnKeyTyped(onlyDigits);
        nodeArchiveTtl2.setOnKeyTyped(onlyDigits);
        nodeKeyProofTtl2.setOnKeyTyped(onlyDigits);
        nodeSyncBefore2.setOnKeyTyped(onlyDigits);

        configNodePublicPort3.setOnKeyTyped(onlyDigits);
        configNodeConsolePort3.setOnKeyTyped(onlyDigits);
        configLiteServerPort3.setOnKeyTyped(onlyDigits);
        validatorWalletDeposit3.setOnKeyTyped(onlyDigits);
        validatorDefaultStake3.setOnKeyTyped(onlyDigits);
        nodeStateTtl3.setOnKeyTyped(onlyDigits);
        nodeBlockTtl3.setOnKeyTyped(onlyDigits);
        nodeArchiveTtl3.setOnKeyTyped(onlyDigits);
        nodeKeyProofTtl3.setOnKeyTyped(onlyDigits);
        nodeSyncBefore3.setOnKeyTyped(onlyDigits);

        configNodePublicPort4.setOnKeyTyped(onlyDigits);
        configNodeConsolePort4.setOnKeyTyped(onlyDigits);
        configLiteServerPort4.setOnKeyTyped(onlyDigits);
        validatorWalletDeposit4.setOnKeyTyped(onlyDigits);
        validatorDefaultStake4.setOnKeyTyped(onlyDigits);
        nodeStateTtl4.setOnKeyTyped(onlyDigits);
        nodeBlockTtl4.setOnKeyTyped(onlyDigits);
        nodeArchiveTtl4.setOnKeyTyped(onlyDigits);
        nodeKeyProofTtl4.setOnKeyTyped(onlyDigits);
        nodeSyncBefore4.setOnKeyTyped(onlyDigits);

        configNodePublicPort5.setOnKeyTyped(onlyDigits);
        configNodeConsolePort5.setOnKeyTyped(onlyDigits);
        configLiteServerPort5.setOnKeyTyped(onlyDigits);
        validatorWalletDeposit5.setOnKeyTyped(onlyDigits);
        validatorDefaultStake5.setOnKeyTyped(onlyDigits);
        nodeStateTtl5.setOnKeyTyped(onlyDigits);
        nodeBlockTtl5.setOnKeyTyped(onlyDigits);
        nodeArchiveTtl5.setOnKeyTyped(onlyDigits);
        nodeKeyProofTtl5.setOnKeyTyped(onlyDigits);
        nodeSyncBefore5.setOnKeyTyped(onlyDigits);

        configNodePublicPort6.setOnKeyTyped(onlyDigits);
        configNodeConsolePort6.setOnKeyTyped(onlyDigits);
        configLiteServerPort6.setOnKeyTyped(onlyDigits);
        validatorWalletDeposit6.setOnKeyTyped(onlyDigits);
        validatorDefaultStake6.setOnKeyTyped(onlyDigits);
        nodeStateTtl6.setOnKeyTyped(onlyDigits);
        nodeBlockTtl6.setOnKeyTyped(onlyDigits);
        nodeArchiveTtl6.setOnKeyTyped(onlyDigits);
        nodeKeyProofTtl6.setOnKeyTyped(onlyDigits);
        nodeSyncBefore6.setOnKeyTyped(onlyDigits);

        configNodePublicPort7.setOnKeyTyped(onlyDigits);
        configNodeConsolePort7.setOnKeyTyped(onlyDigits);
        configLiteServerPort7.setOnKeyTyped(onlyDigits);
        validatorWalletDeposit7.setOnKeyTyped(onlyDigits);
        validatorDefaultStake7.setOnKeyTyped(onlyDigits);
        nodeStateTtl7.setOnKeyTyped(onlyDigits);
        nodeBlockTtl7.setOnKeyTyped(onlyDigits);
        nodeArchiveTtl7.setOnKeyTyped(onlyDigits);
        nodeKeyProofTtl7.setOnKeyTyped(onlyDigits);
        nodeSyncBefore7.setOnKeyTyped(onlyDigits);

        globalId.setOnKeyTyped(onlyDigits);
        initialBalance.setOnKeyTyped(onlyDigits);
        maxMainValidators.setOnKeyTyped(onlyDigits);
        minValidators.setOnKeyTyped(onlyDigits);
        maxValidators.setOnKeyTyped(onlyDigits);
        electedFor.setOnKeyTyped(onlyDigits);
        electionStartBefore.setOnKeyTyped(onlyDigits);
        electionEndBefore.setOnKeyTyped(onlyDigits);
        stakesFrozenFor.setOnKeyTyped(onlyDigits);
        gasPrice.setOnKeyTyped(onlyDigits);
        gasPriceMc.setOnKeyTyped(onlyDigits);
        cellPrice.setOnKeyTyped(onlyDigits);
        cellPriceMc.setOnKeyTyped(onlyDigits);
        minStake.setOnKeyTyped(onlyDigits);
        maxStake.setOnKeyTyped(onlyDigits);
        minTotalStake.setOnKeyTyped(onlyDigits);
        maxFactor.setOnKeyTyped(onlyDigits);
        electionEndBefore.setOnKeyTyped(onlyDigits);

        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                log.debug("search for {}", searchField.getText());

                foundBlockslistviewid.getItems().clear();
                foundTxsvboxid.getItems().clear();
                foundAccountsvboxid.getItems().clear();

                //clear previous results
                mainMenuTabs.getTabs().add(searchTab);
                mainMenuTabs.getSelectionModel().selectLast();
                foundTabs.getTabs().add(foundBlocks);
                foundTabs.getTabs().add(foundAccounts);
                foundTabs.getTabs().add(foundTxs);

                String searchFor = searchField.getText();

                List<BlockEntity> foundBlocksEntities = App.dbPool.searchBlocks(searchFor);
                MyLocalTon.getInstance().showFoundBlocksInGui(foundBlocksEntities, searchFor);

                List<TxEntity> foundTxsEntities = App.dbPool.searchTxs(searchFor);
                MyLocalTon.getInstance().showFoundTxsInGui(((MainController) fxmlLoader.getController()).foundTxs, foundTxsEntities, searchFor, "");

                List<WalletEntity> foundAccountsEntities = App.dbPool.searchAccounts(searchFor);
                MyLocalTon.getInstance().showFoundAccountsInGui(foundAccountsEntities, searchFor);
            }
        });

        mainMenuTabs.getTabs().remove(searchTab);

        foundTabs.getTabs().remove(foundBlocks);
        foundTabs.getTabs().remove(foundAccounts);
        foundTabs.getTabs().remove(foundTxs);

        scrollBtn.setTooltip(new Tooltip("Autoscroll on/off"));

        tickTockCheckBox.setSelected(settings.getUiSettings().isShowTickTockTransactions());
        mainConfigTxCheckBox.setSelected(settings.getUiSettings().isShowMainConfigTransactions());
        inOutMsgsCheckBox.setSelected(settings.getUiSettings().isShowInOutMessages());
        enableBlockchainExplorer.setSelected(settings.getUiSettings().isEnableBlockchainExplorer());
        showMsgBodyCheckBox.setSelected(settings.getUiSettings().isShowBodyInMessage());
        shardStateCheckbox.setSelected(settings.getUiSettings().isShowShardStateInBlockDump());

        walletsNumber.setValue(settings.getWalletSettings().getNumberOfPreinstalledWallets());
        coinsPerWallet.setText(settings.getWalletSettings().getInitialAmount().toString());
        walletVersion.getItems().add(WalletVersion.V1.getValue());
        walletVersion.getItems().add(WalletVersion.V2.getValue());
        walletVersion.getItems().add(WalletVersion.V3.getValue());
        walletVersion.getSelectionModel().select(settings.getWalletSettings().getWalletVersion().getValue());

        validatorLogDir1.setText(settings.getGenesisNode().getTonLogDir());
        myLocalTonLog.setText(settings.LOG_FILE);
        dhtLogDir1.setText(settings.getGenesisNode().getDhtServerDir());

        validatorLogDir2.setText(settings.getNode2().getTonLogDir());
        validatorLogDir3.setText(settings.getNode3().getTonLogDir());
        validatorLogDir4.setText(settings.getNode4().getTonLogDir());
        validatorLogDir5.setText(settings.getNode5().getTonLogDir());
        validatorLogDir6.setText(settings.getNode6().getTonLogDir());
        validatorLogDir7.setText(settings.getNode7().getTonLogDir());

        minValidators.setText(settings.getBlockchainSettings().getMinValidators().toString());
        maxValidators.setText(settings.getBlockchainSettings().getMaxValidators().toString());
        maxMainValidators.setText(settings.getBlockchainSettings().getMaxMainValidators().toString());

        electedFor.setText(settings.getBlockchainSettings().getElectedFor().toString());
        electionStartBefore.setText(settings.getBlockchainSettings().getElectionStartBefore().toString());
        electionEndBefore.setText(settings.getBlockchainSettings().getElectionEndBefore().toString());
        stakesFrozenFor.setText(settings.getBlockchainSettings().getElectionStakesFrozenFor().toString());

        globalId.setText(settings.getBlockchainSettings().getGlobalId().toString());
        initialBalance.setText(settings.getBlockchainSettings().getInitialBalance().toString());
        gasPrice.setText(settings.getBlockchainSettings().getGasPrice().toString());
        gasPriceMc.setText(settings.getBlockchainSettings().getGasPriceMc().toString());
        cellPrice.setText(settings.getBlockchainSettings().getCellPrice().toString());
        cellPriceMc.setText(settings.getBlockchainSettings().getCellPriceMc().toString());

        minStake.setText(settings.getBlockchainSettings().getMinValidatorStake().toString());
        maxStake.setText(settings.getBlockchainSettings().getMaxValidatorStake().toString());
        minTotalStake.setText(settings.getBlockchainSettings().getMinTotalValidatorStake().toString());
        maxFactor.setText(settings.getBlockchainSettings().getMaxFactor().toString());

        nodeBlockTtl1.setText(settings.getGenesisNode().getValidatorBlockTtl().toString());
        nodeArchiveTtl1.setText(settings.getGenesisNode().getValidatorArchiveTtl().toString());
        nodeKeyProofTtl1.setText(settings.getGenesisNode().getValidatorKeyProofTtl().toString());
        nodeStateTtl1.setText(settings.getGenesisNode().getValidatorStateTtl().toString());
        nodeSyncBefore1.setText(settings.getGenesisNode().getValidatorSyncBefore().toString());
        configNodePublicPort1.setText(settings.getGenesisNode().getPublicPort().toString());
        configNodeConsolePort1.setText(settings.getGenesisNode().getConsolePort().toString());
        configLiteServerPort1.setText(settings.getGenesisNode().getLiteServerPort().toString());
        configDhtServerPort1.setText(settings.getGenesisNode().getDhtPort().toString());
        validatorWalletDeposit1.setText(settings.getGenesisNode().getInitialValidatorWalletAmount().toString());
        validatorDefaultStake1.setText(settings.getGenesisNode().getDefaultValidatorStake().toString());

        nodeBlockTtl2.setText(settings.getNode2().getValidatorBlockTtl().toString());
        nodeArchiveTtl2.setText(settings.getNode2().getValidatorArchiveTtl().toString());
        nodeKeyProofTtl2.setText(settings.getNode2().getValidatorKeyProofTtl().toString());
        nodeStateTtl2.setText(settings.getNode2().getValidatorStateTtl().toString());
        nodeSyncBefore2.setText(settings.getNode2().getValidatorSyncBefore().toString());
        configNodePublicPort2.setText(settings.getNode2().getPublicPort().toString());
        configNodeConsolePort2.setText(settings.getNode2().getConsolePort().toString());
        configLiteServerPort2.setText(settings.getNode2().getLiteServerPort().toString());
        validatorWalletDeposit2.setText(settings.getNode2().getInitialValidatorWalletAmount().toString());
        validatorDefaultStake2.setText(settings.getNode2().getDefaultValidatorStake().toString());

        nodeBlockTtl3.setText(settings.getNode3().getValidatorBlockTtl().toString());
        nodeArchiveTtl3.setText(settings.getNode3().getValidatorArchiveTtl().toString());
        nodeKeyProofTtl3.setText(settings.getNode3().getValidatorKeyProofTtl().toString());
        nodeStateTtl3.setText(settings.getNode3().getValidatorStateTtl().toString());
        nodeSyncBefore3.setText(settings.getNode3().getValidatorSyncBefore().toString());
        configNodePublicPort3.setText(settings.getNode3().getPublicPort().toString());
        configNodeConsolePort3.setText(settings.getNode3().getConsolePort().toString());
        configLiteServerPort3.setText(settings.getNode3().getLiteServerPort().toString());
        validatorWalletDeposit3.setText(settings.getNode3().getInitialValidatorWalletAmount().toString());
        validatorDefaultStake3.setText(settings.getNode3().getDefaultValidatorStake().toString());

        nodeBlockTtl4.setText(settings.getNode4().getValidatorBlockTtl().toString());
        nodeArchiveTtl4.setText(settings.getNode4().getValidatorArchiveTtl().toString());
        nodeKeyProofTtl4.setText(settings.getNode4().getValidatorKeyProofTtl().toString());
        nodeStateTtl4.setText(settings.getNode4().getValidatorStateTtl().toString());
        nodeSyncBefore4.setText(settings.getNode4().getValidatorSyncBefore().toString());
        configNodePublicPort4.setText(settings.getNode4().getPublicPort().toString());
        configNodeConsolePort4.setText(settings.getNode4().getConsolePort().toString());
        configLiteServerPort4.setText(settings.getNode4().getLiteServerPort().toString());
        validatorWalletDeposit4.setText(settings.getNode4().getInitialValidatorWalletAmount().toString());
        validatorDefaultStake4.setText(settings.getNode4().getDefaultValidatorStake().toString());

        nodeBlockTtl5.setText(settings.getNode5().getValidatorBlockTtl().toString());
        nodeArchiveTtl5.setText(settings.getNode5().getValidatorArchiveTtl().toString());
        nodeKeyProofTtl5.setText(settings.getNode5().getValidatorKeyProofTtl().toString());
        nodeStateTtl5.setText(settings.getNode5().getValidatorStateTtl().toString());
        nodeSyncBefore5.setText(settings.getNode5().getValidatorSyncBefore().toString());
        configNodePublicPort5.setText(settings.getNode5().getPublicPort().toString());
        configNodeConsolePort5.setText(settings.getNode5().getConsolePort().toString());
        configLiteServerPort5.setText(settings.getNode5().getLiteServerPort().toString());
        validatorWalletDeposit5.setText(settings.getNode5().getInitialValidatorWalletAmount().toString());
        validatorDefaultStake5.setText(settings.getNode5().getDefaultValidatorStake().toString());

        nodeBlockTtl6.setText(settings.getNode6().getValidatorBlockTtl().toString());
        nodeArchiveTtl6.setText(settings.getNode6().getValidatorArchiveTtl().toString());
        nodeKeyProofTtl6.setText(settings.getNode6().getValidatorKeyProofTtl().toString());
        nodeStateTtl6.setText(settings.getNode6().getValidatorStateTtl().toString());
        nodeSyncBefore6.setText(settings.getNode6().getValidatorSyncBefore().toString());
        configNodePublicPort6.setText(settings.getNode6().getPublicPort().toString());
        configNodeConsolePort6.setText(settings.getNode6().getConsolePort().toString());
        configLiteServerPort6.setText(settings.getNode6().getLiteServerPort().toString());
        validatorWalletDeposit6.setText(settings.getNode6().getInitialValidatorWalletAmount().toString());
        validatorDefaultStake6.setText(settings.getNode6().getDefaultValidatorStake().toString());

        nodeBlockTtl7.setText(settings.getNode7().getValidatorBlockTtl().toString());
        nodeArchiveTtl7.setText(settings.getNode7().getValidatorArchiveTtl().toString());
        nodeKeyProofTtl7.setText(settings.getNode7().getValidatorKeyProofTtl().toString());
        nodeStateTtl7.setText(settings.getNode7().getValidatorStateTtl().toString());
        nodeSyncBefore7.setText(settings.getNode7().getValidatorSyncBefore().toString());
        configNodePublicPort7.setText(settings.getNode7().getPublicPort().toString());
        configNodeConsolePort7.setText(settings.getNode7().getConsolePort().toString());
        configLiteServerPort7.setText(settings.getNode7().getLiteServerPort().toString());
        validatorWalletDeposit7.setText(settings.getNode7().getInitialValidatorWalletAmount().toString());
        validatorDefaultStake7.setText(settings.getNode7().getDefaultValidatorStake().toString());

        tonLogLevel.getItems().add("DEBUG");
        tonLogLevel.getItems().add("WARNING");
        tonLogLevel.getItems().add("INFO");
        tonLogLevel.getItems().add("ERROR");
        tonLogLevel.getItems().add("FATAL");
        tonLogLevel.getSelectionModel().select(settings.getGenesisNode().getTonLogLevel());

        tonLogLevel2.getItems().add("DEBUG");
        tonLogLevel2.getItems().add("WARNING");
        tonLogLevel2.getItems().add("INFO");
        tonLogLevel2.getItems().add("ERROR");
        tonLogLevel2.getItems().add("FATAL");
        tonLogLevel2.getSelectionModel().select(settings.getNode2().getTonLogLevel());

        tonLogLevel3.getItems().add("DEBUG");
        tonLogLevel3.getItems().add("WARNING");
        tonLogLevel3.getItems().add("INFO");
        tonLogLevel3.getItems().add("ERROR");
        tonLogLevel3.getItems().add("FATAL");
        tonLogLevel3.getSelectionModel().select(settings.getNode3().getTonLogLevel());

        tonLogLevel4.getItems().add("DEBUG");
        tonLogLevel4.getItems().add("WARNING");
        tonLogLevel4.getItems().add("INFO");
        tonLogLevel4.getItems().add("ERROR");
        tonLogLevel4.getItems().add("FATAL");
        tonLogLevel4.getSelectionModel().select(settings.getNode4().getTonLogLevel());

        tonLogLevel5.getItems().add("DEBUG");
        tonLogLevel5.getItems().add("WARNING");
        tonLogLevel5.getItems().add("INFO");
        tonLogLevel5.getItems().add("ERROR");
        tonLogLevel5.getItems().add("FATAL");
        tonLogLevel5.getSelectionModel().select(settings.getNode5().getTonLogLevel());

        tonLogLevel6.getItems().add("DEBUG");
        tonLogLevel6.getItems().add("WARNING");
        tonLogLevel6.getItems().add("INFO");
        tonLogLevel6.getItems().add("ERROR");
        tonLogLevel6.getItems().add("FATAL");
        tonLogLevel6.getSelectionModel().select(settings.getNode6().getTonLogLevel());

        tonLogLevel7.getItems().add("DEBUG");
        tonLogLevel7.getItems().add("WARNING");
        tonLogLevel7.getItems().add("INFO");
        tonLogLevel7.getItems().add("ERROR");
        tonLogLevel7.getItems().add("FATAL");
        tonLogLevel7.getSelectionModel().select(settings.getNode7().getTonLogLevel());

        myLogLevel.getItems().add("INFO");
        myLogLevel.getItems().add("DEBUG");
        myLogLevel.getItems().add("ERROR");
        myLogLevel.getSelectionModel().select(settings.getGenesisNode().getMyLocalTonLogLevel());

        // blockchain-explorer tab
        enableBlockchainExplorer.setVisible(false);
        enableBlockchainExplorerLabel.setVisible(false);
        mainMenuTabs.getTabs().remove(explorerTab);

        if (isLinux() || isMac()) {

            enableBlockchainExplorer.setVisible(true);
            enableBlockchainExplorerLabel.setVisible(true);

            if (enableBlockchainExplorer.isSelected()) {
                mainMenuTabs.getTabs().remove(searchTab);
                mainMenuTabs.getTabs().remove(explorerTab);
                mainMenuTabs.getTabs().add(explorerTab);
            } else {
                mainMenuTabs.getTabs().remove(explorerTab);
            }
        }

        if (isLinux() || isMac()) {
            addValidatorBtn.setVisible(true);
        }

        // validator-tabs
        validationTabs.getTabs().remove(validator2tab);
        validationTabs.getTabs().remove(validator3tab);
        validationTabs.getTabs().remove(validator4tab);
        validationTabs.getTabs().remove(validator5tab);
        validationTabs.getTabs().remove(validator6tab);
        validationTabs.getTabs().remove(validator7tab);

        for (String n : Arrays.asList("node2", "node3", "node4", "node5", "node6", "node7")) {
            if (settings.getActiveNodes().contains(n)) {
                validationTabs.getTabs().add(getNodeTabByName(n));
            }
        }
    }

    public Tab getNodeTabByName(String nodeName) {
        switch (nodeName) {
            case "genesis":
                return genesisnode1;
            case "node2":
                return validator2tab;
            case "node3":
                return validator3tab;
            case "node4":
                return validator4tab;
            case "node5":
                return validator5tab;
            case "node6":
                return validator6tab;
            case "node7":
                return validator7tab;
            default:
                return null;
        }
    }

    public void startWeb() {

        if (isLinux() || isMac()) {
            if (enableBlockchainExplorer.isSelected()) {
                log.info("Starting native blockchain-explorer on port {}", settings.getUiSettings().getBlockchainExplorerPort());
                BlockchainExplorer blockchainExplorer = new BlockchainExplorer();
                blockchainExplorer.startBlockchainExplorer(settings.getGenesisNode(), settings.getGenesisNode().getNodeGlobalConfigLocation(), settings.getUiSettings().getBlockchainExplorerPort());
                WebEngine webEngine = webView.getEngine();
                webEngine.load("http://127.0.0.1:" + settings.getUiSettings().getBlockchainExplorerPort() + "/last");
            }
        }
    }

    public void showAccTxs(String hexAddr) throws IOException {

        mainMenuTabs.getTabs().remove(searchTab);
        mainMenuTabs.getTabs().add(searchTab);
        mainMenuTabs.getSelectionModel().selectLast();

        if (!foundTabs.getTabs().filtered(t -> t.getText().contains(Utils.getLightAddress(hexAddr))).isEmpty()) {
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("foundtxstab.fxml"));
        Tab newTab = fxmlLoader.load();

        newTab.setOnClosed(event -> {
            if (foundTabs.getTabs().isEmpty()) {
                mainMenuTabs.getTabs().remove(searchTab);
                mainMenuTabs.getSelectionModel().selectFirst();
            }
        });

        foundTabs.getTabs().add(newTab);

        List<TxEntity> foundTxsEntities = App.dbPool.searchTxs(hexAddr);
        MyLocalTon.getInstance().showFoundTxsInGui(newTab, foundTxsEntities, hexAddr, hexAddr);
        foundTabs.getSelectionModel().selectLast();
    }

    public void saveSettings() {
        log.debug("saving all settings");
        settings.getUiSettings().setShowTickTockTransactions(tickTockCheckBox.isSelected());
        settings.getUiSettings().setShowMainConfigTransactions(mainConfigTxCheckBox.isSelected());
        settings.getUiSettings().setShowInOutMessages(inOutMsgsCheckBox.isSelected());
        settings.getUiSettings().setShowBodyInMessage(showMsgBodyCheckBox.isSelected());
        settings.getUiSettings().setEnableBlockchainExplorer(enableBlockchainExplorer.isSelected());
        settings.getUiSettings().setShowShardStateInBlockDump(shardStateCheckbox.isSelected());

        settings.getWalletSettings().setNumberOfPreinstalledWallets((long) walletsNumber.getValue());
        settings.getWalletSettings().setInitialAmount(new BigDecimal(coinsPerWallet.getText()));
        settings.getWalletSettings().setWalletVersion(WalletVersion.getKeyByValue(walletVersion.getValue()));

        settings.getBlockchainSettings().setMinValidators(Long.valueOf(minValidators.getText()));
        settings.getBlockchainSettings().setMaxValidators(Long.valueOf(maxValidators.getText()));
        settings.getBlockchainSettings().setMaxMainValidators(Long.valueOf(maxMainValidators.getText()));

        settings.getBlockchainSettings().setGlobalId(Long.valueOf(globalId.getText()));
        settings.getBlockchainSettings().setInitialBalance(Long.valueOf(initialBalance.getText()));

        settings.getBlockchainSettings().setElectedFor(Long.valueOf(electedFor.getText()));
        settings.getBlockchainSettings().setElectionStartBefore(Long.valueOf(electionStartBefore.getText()));
        settings.getBlockchainSettings().setElectionEndBefore(Long.valueOf(electionEndBefore.getText()));
        settings.getBlockchainSettings().setElectionStakesFrozenFor(Long.valueOf(stakesFrozenFor.getText()));
        settings.getBlockchainSettings().setGasPrice(Long.valueOf(gasPrice.getText()));
        settings.getBlockchainSettings().setGasPriceMc(Long.valueOf(gasPriceMc.getText()));
        settings.getBlockchainSettings().setCellPrice(Long.valueOf(cellPrice.getText()));
        settings.getBlockchainSettings().setCellPriceMc(Long.valueOf(cellPriceMc.getText()));

        settings.getBlockchainSettings().setMinValidatorStake(Long.valueOf(minStake.getText()));
        settings.getBlockchainSettings().setMaxValidatorStake(Long.valueOf(maxStake.getText()));
        settings.getBlockchainSettings().setMinTotalValidatorStake(Long.valueOf(minTotalStake.getText()));
        settings.getBlockchainSettings().setMaxFactor(new BigDecimal(maxFactor.getText()));

        settings.getGenesisNode().setValidatorBlockTtl(Long.valueOf(nodeBlockTtl1.getText()));
        settings.getGenesisNode().setValidatorArchiveTtl(Long.valueOf(nodeArchiveTtl1.getText()));
        settings.getGenesisNode().setValidatorKeyProofTtl(Long.valueOf(nodeKeyProofTtl1.getText()));
        settings.getGenesisNode().setValidatorStateTtl(Long.valueOf(nodeStateTtl1.getText()));
        settings.getGenesisNode().setValidatorSyncBefore(Long.valueOf(nodeSyncBefore1.getText()));
        settings.getGenesisNode().setMyLocalTonLogLevel(myLogLevel.getValue());
        settings.getGenesisNode().setPublicPort(Integer.valueOf(configNodePublicPort1.getText()));
        settings.getGenesisNode().setConsolePort(Integer.valueOf(configNodeConsolePort1.getText()));
        settings.getGenesisNode().setLiteServerPort(Integer.valueOf(configLiteServerPort1.getText()));
        settings.getGenesisNode().setDhtPort(Integer.valueOf(configDhtServerPort1.getText()));
        settings.getGenesisNode().setInitialValidatorWalletAmount(new BigDecimal(validatorWalletDeposit1.getText()));
        settings.getGenesisNode().setDefaultValidatorStake(new BigDecimal(validatorDefaultStake1.getText()));

        settings.getNode2().setValidatorBlockTtl(Long.valueOf(nodeBlockTtl2.getText()));
        settings.getNode2().setValidatorArchiveTtl(Long.valueOf(nodeArchiveTtl2.getText()));
        settings.getNode2().setValidatorKeyProofTtl(Long.valueOf(nodeKeyProofTtl2.getText()));
        settings.getNode2().setValidatorStateTtl(Long.valueOf(nodeStateTtl2.getText()));
        settings.getNode2().setValidatorSyncBefore(Long.valueOf(nodeSyncBefore2.getText()));
        settings.getNode2().setPublicPort(Integer.valueOf(configNodePublicPort2.getText()));
        settings.getNode2().setConsolePort(Integer.valueOf(configNodeConsolePort2.getText()));
        settings.getNode2().setLiteServerPort(Integer.valueOf(configLiteServerPort2.getText()));
        settings.getNode2().setInitialValidatorWalletAmount(new BigDecimal(validatorWalletDeposit2.getText()));
        settings.getNode2().setDefaultValidatorStake(new BigDecimal(validatorDefaultStake2.getText()));
        settings.getNode2().setTonLogLevel(tonLogLevel2.getValue());

        settings.getNode3().setValidatorBlockTtl(Long.valueOf(nodeBlockTtl3.getText()));
        settings.getNode3().setValidatorArchiveTtl(Long.valueOf(nodeArchiveTtl3.getText()));
        settings.getNode3().setValidatorKeyProofTtl(Long.valueOf(nodeKeyProofTtl3.getText()));
        settings.getNode3().setValidatorStateTtl(Long.valueOf(nodeStateTtl3.getText()));
        settings.getNode3().setValidatorSyncBefore(Long.valueOf(nodeSyncBefore3.getText()));
        settings.getNode3().setPublicPort(Integer.valueOf(configNodePublicPort3.getText()));
        settings.getNode3().setConsolePort(Integer.valueOf(configNodeConsolePort3.getText()));
        settings.getNode3().setLiteServerPort(Integer.valueOf(configLiteServerPort3.getText()));
        settings.getNode3().setInitialValidatorWalletAmount(new BigDecimal(validatorWalletDeposit3.getText()));
        settings.getNode3().setDefaultValidatorStake(new BigDecimal(validatorDefaultStake3.getText()));
        settings.getNode3().setTonLogLevel(tonLogLevel3.getValue());

        settings.getNode4().setValidatorBlockTtl(Long.valueOf(nodeBlockTtl4.getText()));
        settings.getNode4().setValidatorArchiveTtl(Long.valueOf(nodeArchiveTtl4.getText()));
        settings.getNode4().setValidatorKeyProofTtl(Long.valueOf(nodeKeyProofTtl4.getText()));
        settings.getNode4().setValidatorStateTtl(Long.valueOf(nodeStateTtl4.getText()));
        settings.getNode4().setValidatorSyncBefore(Long.valueOf(nodeSyncBefore4.getText()));
        settings.getNode4().setPublicPort(Integer.valueOf(configNodePublicPort4.getText()));
        settings.getNode4().setConsolePort(Integer.valueOf(configNodeConsolePort4.getText()));
        settings.getNode4().setLiteServerPort(Integer.valueOf(configLiteServerPort4.getText()));
        settings.getNode4().setInitialValidatorWalletAmount(new BigDecimal(validatorWalletDeposit4.getText()));
        settings.getNode4().setDefaultValidatorStake(new BigDecimal(validatorDefaultStake4.getText()));
        settings.getNode4().setTonLogLevel(tonLogLevel4.getValue());

        settings.getNode5().setValidatorBlockTtl(Long.valueOf(nodeBlockTtl5.getText()));
        settings.getNode5().setValidatorArchiveTtl(Long.valueOf(nodeArchiveTtl5.getText()));
        settings.getNode5().setValidatorKeyProofTtl(Long.valueOf(nodeKeyProofTtl5.getText()));
        settings.getNode5().setValidatorStateTtl(Long.valueOf(nodeStateTtl5.getText()));
        settings.getNode5().setValidatorSyncBefore(Long.valueOf(nodeSyncBefore5.getText()));
        settings.getNode5().setPublicPort(Integer.valueOf(configNodePublicPort5.getText()));
        settings.getNode5().setConsolePort(Integer.valueOf(configNodeConsolePort5.getText()));
        settings.getNode5().setLiteServerPort(Integer.valueOf(configLiteServerPort5.getText()));
        settings.getNode5().setInitialValidatorWalletAmount(new BigDecimal(validatorWalletDeposit5.getText()));
        settings.getNode5().setDefaultValidatorStake(new BigDecimal(validatorDefaultStake5.getText()));
        settings.getNode5().setTonLogLevel(tonLogLevel5.getValue());

        settings.getNode6().setValidatorBlockTtl(Long.valueOf(nodeBlockTtl6.getText()));
        settings.getNode6().setValidatorArchiveTtl(Long.valueOf(nodeArchiveTtl6.getText()));
        settings.getNode6().setValidatorKeyProofTtl(Long.valueOf(nodeKeyProofTtl6.getText()));
        settings.getNode6().setValidatorStateTtl(Long.valueOf(nodeStateTtl6.getText()));
        settings.getNode6().setValidatorSyncBefore(Long.valueOf(nodeSyncBefore6.getText()));
        settings.getNode6().setPublicPort(Integer.valueOf(configNodePublicPort6.getText()));
        settings.getNode6().setConsolePort(Integer.valueOf(configNodeConsolePort6.getText()));
        settings.getNode6().setLiteServerPort(Integer.valueOf(configLiteServerPort6.getText()));
        settings.getNode6().setInitialValidatorWalletAmount(new BigDecimal(validatorWalletDeposit6.getText()));
        settings.getNode6().setDefaultValidatorStake(new BigDecimal(validatorDefaultStake6.getText()));
        settings.getNode6().setTonLogLevel(tonLogLevel6.getValue());

        settings.getNode7().setValidatorBlockTtl(Long.valueOf(nodeBlockTtl7.getText()));
        settings.getNode7().setValidatorArchiveTtl(Long.valueOf(nodeArchiveTtl7.getText()));
        settings.getNode7().setValidatorKeyProofTtl(Long.valueOf(nodeKeyProofTtl7.getText()));
        settings.getNode7().setValidatorStateTtl(Long.valueOf(nodeStateTtl7.getText()));
        settings.getNode7().setValidatorSyncBefore(Long.valueOf(nodeSyncBefore7.getText()));
        settings.getNode7().setPublicPort(Integer.valueOf(configNodePublicPort7.getText()));
        settings.getNode7().setConsolePort(Integer.valueOf(configNodeConsolePort7.getText()));
        settings.getNode7().setLiteServerPort(Integer.valueOf(configLiteServerPort7.getText()));
        settings.getNode7().setInitialValidatorWalletAmount(new BigDecimal(validatorWalletDeposit7.getText()));
        settings.getNode7().setDefaultValidatorStake(new BigDecimal(validatorDefaultStake7.getText()));
        settings.getNode7().setTonLogLevel(tonLogLevel7.getValue());

        Utils.saveSettingsToGson(settings);
    }

    public void accountsOnScroll(ScrollEvent scrollEvent) {
        log.debug("accountsOnScroll");
    }

    public void foundBlocksOnScroll(ScrollEvent scrollEvent) {
        log.debug("foundBlocksOnScroll");
    }

    public void foundTxsOnScroll(ScrollEvent scrollEvent) {
        log.debug("foundTxsOnScroll");
    }

    public void liteServerClicked() throws IOException {
        String lastCommand = LiteClient.getInstance(LiteClientEnum.GLOBAL).getLastCommand(MyLocalTon.getInstance().getSettings().getGenesisNode());
        log.info("show console with last command, {}", lastCommand);

        if (isWindows()) {
            log.info("cmd /c start cmd.exe /k \"echo " + lastCommand + " && " + lastCommand + "\"");
            Runtime.getRuntime().exec("cmd /c start cmd.exe /k \"echo " + lastCommand + " && " + lastCommand + "\"");
        } else if (isLinux()) {
            if (Files.exists(Paths.get("/usr/bin/xterm"))) {
                log.info("/usr/bin/xterm -hold -geometry 200 -e " + lastCommand);
                Runtime.getRuntime().exec("/usr/bin/xterm -hold -geometry 200 -e " + lastCommand);
            } else {
                log.info("xterm is not installed");
            }
        } else {
            //log.info("zsh -c \"" + lastCommand + "\"");
            //Runtime.getRuntime().exec("zsh -c \"" + lastCommand + "\"");
            log.debug("terminal call not implemented");
        }

        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(lastCommand);
        clipboard.setContent(content);
        log.debug(lastCommand + " copied");
        App.mainController.showInfoMsg("lite-client last command copied to clipboard", 0.5);
    }

    public void resetAction() throws IOException {

        Parent parent = new FXMLLoader(App.class.getClassLoader().getResource("org/ton/main/yesnodialog.fxml")).load();
        parent.lookup("#inputFields").setVisible(false);
        parent.lookup("#body").setVisible(true);
        parent.lookup("#header").setVisible(true);
        ((Label) parent.lookup("#action")).setText("reset");
        ((Label) parent.lookup("#header")).setText("Reset TON blockchain");
        ((Label) parent.lookup("#body")).setText("You can reset current single-node TON blockchain to the new settings. All data will be lost and zero state will be created from scratch. Do you want to proceed?");
        parent.lookup("#okBtn").setDisable(false);

        JFXDialogLayout content = new JFXDialogLayout();
        content.setBody(parent);

        yesNoDialog = new JFXDialog(superWindow, content, JFXDialog.DialogTransition.CENTER);
        yesNoDialog.setOnKeyPressed(keyEvent -> {
                    if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                        yesNoDialog.close();
                    }
                }
        );

        yesNoDialog.show();
    }

    public void showDialogMessage(String header, String body) {
        Platform.runLater(() -> {
            try {
                Parent parent = new FXMLLoader(App.class.getClassLoader().getResource("org/ton/main/yesnodialog.fxml")).load();

                parent.lookup("#inputFields").setVisible(false);
                parent.lookup("#body").setVisible(true);
                parent.lookup("#header").setVisible(true);
                //((Label) parent.lookup("#action")).setText("reset"); // no action, simple dialog box
                ((Label) parent.lookup("#header")).setText(header);
                ((Label) parent.lookup("#body")).setText(body);
                parent.lookup("#okBtn").setDisable(false);

                JFXDialogLayout content = new JFXDialogLayout();
                content.setBody(parent);

                yesNoDialog = new JFXDialog(superWindow, content, JFXDialog.DialogTransition.CENTER);
                yesNoDialog.setOnKeyPressed(keyEvent -> {
                            if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                                yesNoDialog.close();
                            }
                        }
                );
                yesNoDialog.show();
            } catch (IOException e) {
                log.error("Cannot load resource org/ton/main/yesnodialog.fxml");
                e.printStackTrace();
            }
        });
    }

    public void showDialogConfirmDeleteNode(org.ton.settings.Node node) {
        Platform.runLater(() -> {
            try {
                Parent parent = new FXMLLoader(App.class.getClassLoader().getResource("org/ton/main/yesnodialog.fxml")).load();

                String stopsWokring = "";
                MyLocalTonSettings settings = MyLocalTon.getInstance().getSettings();
                int cutoff = (int) Math.ceil(settings.getActiveNodes().size() * 66 / 100.0);
                log.info("total active nodes {} vs minimum required {}", settings.getActiveNodes().size(), cutoff);
                if ((settings.getActiveNodes().size() - 1 < cutoff) || (settings.getActiveNodes().size() == 3 && cutoff == 2)) {
                    stopsWokring = "\n\nIf you delete this node your main workchain becomes inactive, i.e. stops working, since a two-thirds consensus of validators will not be reached.";
                }

                parent.lookup("#inputFields").setVisible(false);
                parent.lookup("#body").setVisible(true);
                parent.lookup("#header").setVisible(true);
                ((Label) parent.lookup("#action")).setText("delnode"); // no action, simple dialog box
                ((Label) parent.lookup("#header")).setText("Confirmation");
                ((Label) parent.lookup("#address")).setText(node.getNodeName()); // just reuse address field
                ((Label) parent.lookup("#body")).setText("Are you sure you want to delete selected validator? All data and funds will be lost and obviously validator will be removed from elections. Also check if this validator has collected all validation rewards." + stopsWokring);
                parent.lookup("#okBtn").setDisable(false);

                JFXDialogLayout content = new JFXDialogLayout();
                content.setBody(parent);

                yesNoDialog = new JFXDialog(superWindow, content, JFXDialog.DialogTransition.CENTER);
                yesNoDialog.setOnKeyPressed(keyEvent -> {
                            if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                                yesNoDialog.close();
                            }
                        }
                );
                yesNoDialog.show();
            } catch (IOException e) {
                log.error("Cannot load resource org/ton/main/yesnodialog.fxml");
                e.printStackTrace();
            }
        });
    }

    /*
    public void transformAction() throws IOException {

        Parent parent = new FXMLLoader(App.class.getClassLoader().getResource("org/ton/main/yesnodialog.fxml")).load();
        parent.lookup("#inputFields").setVisible(false);
        parent.lookup("#body").setVisible(true);
        parent.lookup("#header").setVisible(true);
        ((Label) parent.lookup("#action")).setText("transform");
        ((Label) parent.lookup("#header")).setText("Transform");
        ((Label) parent.lookup("#body")).setText("You can transform this single-node TON blockchain into three-nodes TON blockchain, where all three nodes will act as validators and participate in elections. " +
                "Later you will be able to add more full nodes if you wish. Do you want to proceed?");
        parent.lookup("#okBtn").setDisable(true);

        JFXDialogLayout content = new JFXDialogLayout();
        content.setBody(parent);

        yesNoDialog = new JFXDialog(superWindow, content, JFXDialog.DialogTransition.CENTER);
        yesNoDialog.setOnKeyPressed(keyEvent -> {
                    if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                        yesNoDialog.close();
                    }
                }
        );

        yesNoDialog.show();
    }
    */
    public void showMessage(String msg) {

        try {

            Parent parent = new FXMLLoader(App.class.getClassLoader().getResource("org/ton/main/yesnodialog.fxml")).load();
            parent.lookup("#inputFields").setVisible(false);
            parent.lookup("#body").setVisible(true);
            parent.lookup("#header").setVisible(true);
            ((Label) parent.lookup("#action")).setText("showmsg");
            ((Label) parent.lookup("#header")).setText("Message");
            ((Label) parent.lookup("#body")).setText(msg);
            parent.lookup("#okBtn").setDisable(false);
            ((JFXButton) parent.lookup("#okBtn")).setText("Close");

            JFXDialogLayout content = new JFXDialogLayout();
            content.setBody(parent);

            yesNoDialog = new JFXDialog(superWindow, content, JFXDialog.DialogTransition.CENTER);
            yesNoDialog.setOnKeyPressed(keyEvent -> {
                        if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                            yesNoDialog.close();
                        }
                    }
            );

            yesNoDialog.show();
        } catch (Exception e) {
            log.error("Cannot show message, error {}", e.getMessage());
        }
    }

    public void createNewAccountBtn() throws IOException {
        log.info("create account btn");

        Parent parent = new FXMLLoader(App.class.getClassLoader().getResource("org/ton/main/yesnodialog.fxml")).load();
        ((Label) parent.lookup("#action")).setText("create");
        ((Label) parent.lookup("#header")).setText("Create " + settings.getWalletSettings().getWalletVersion());
        parent.lookup("#body").setVisible(false);
        parent.lookup("#inputFields").setVisible(true);
        if (settings.getWalletSettings().getWalletVersion().equals(WalletVersion.V3)) {
            parent.lookup("#workchain").setVisible(true);
            parent.lookup("#subWalletId").setVisible(true);
        } else {
            parent.lookup("#workchain").setVisible(true);
            parent.lookup("#subWalletId").setVisible(false);
        }
        parent.lookup("#okBtn").setDisable(false);

        JFXDialogLayout content = new JFXDialogLayout();
        content.setBody(parent);

        yesNoDialog = new JFXDialog(superWindow, content, JFXDialog.DialogTransition.CENTER);
        yesNoDialog.setOnKeyPressed(keyEvent -> {
                    if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                        yesNoDialog.close();
                    }
                }
        );

        yesNoDialog.show();
    }

    public void updateValidationTabInfo(ValidationParam v) {
        try {
            LiteClient liteClient = LiteClient.getInstance(LiteClientEnum.GLOBAL);
            ResultConfig34 config34 = LiteClientParser.parseConfig34(liteClient.executeGetCurrentValidators(settings.getGenesisNode()));
            ResultConfig32 config32 = LiteClientParser.parseConfig32(liteClient.executeGetPreviousValidators(settings.getGenesisNode()));
            ResultConfig36 config36 = LiteClientParser.parseConfig36(liteClient.executeGetNextValidators(settings.getGenesisNode()));

            totalValidators.setText(config32.getValidators().getValidators().size() + " / " + config34.getValidators().getValidators().size() + " / " + config36.getValidators().getValidators().size());
            String previous = "Previous validators (Public key, ADNL address, weight): " + System.lineSeparator() + config32.getValidators().getValidators().stream().map(i -> i.getPublicKey() + "  " + i.getAdnlAddress() + "  " + i.getWeight()).collect(Collectors.joining(System.lineSeparator())) + System.lineSeparator() + System.lineSeparator();
            String current = "Current validators: " + System.lineSeparator() + config34.getValidators().getValidators().stream().map(i -> i.getPublicKey() + "  " + i.getAdnlAddress() + "  " + i.getWeight()).collect(Collectors.joining(System.lineSeparator())) + System.lineSeparator() + System.lineSeparator();
            String next = "Next validators (available only within a Break time): " + System.lineSeparator() + config36.getValidators().getValidators().stream().map(i -> i.getPublicKey() + "  " + i.getAdnlAddress() + "  " + i.getWeight()).collect(Collectors.joining(System.lineSeparator())) + System.lineSeparator();
            totalValidators.setTooltip(new Tooltip(previous + current + next));

            blockchainLaunched.setText(Utils.toLocal(v.getBlockchainLaunchTime()));

            colorValidationTiming(v);

            long validationStartInAgoSeconds = Math.abs(Utils.getCurrentTimeSeconds() - v.getStartValidationCycle());
            String startsValidationDuration = DurationFormatUtils.formatDuration(java.time.Duration.ofSeconds(validationStartInAgoSeconds).toMillis(), "HH:mm:ss", true);
            if ((Utils.getCurrentTimeSeconds() - v.getStartValidationCycle()) > 0) {
                startCycle.setText(Utils.toLocal(v.getStartValidationCycle()) + "  Started ago (" + startsValidationDuration + ")");
            } else {
                startCycle.setText(Utils.toLocal(v.getStartValidationCycle()) + "  Starts in (" + startsValidationDuration + ")");
            }
            long validationDurationInSeconds = v.getEndValidationCycle() - v.getStartValidationCycle();
            String validation1Duration = DurationFormatUtils.formatDuration(java.time.Duration.ofSeconds(validationDurationInSeconds).toMillis(), "HH:mm:ss", true);
            endCycle.setText(Utils.toLocal(v.getEndValidationCycle()) + "  Duration (" + validation1Duration + ")");

            long electionsStartsInAgoSeconds = Math.abs(Utils.getCurrentTimeSeconds() - v.getStartElections());
            String startsElectionDuration = DurationFormatUtils.formatDuration(java.time.Duration.ofSeconds(electionsStartsInAgoSeconds).toMillis(), "HH:mm:ss", true);
            if ((Utils.getCurrentTimeSeconds() - v.getStartElections()) > 0) {
                startElections.setText(Utils.toLocal(v.getStartElections()) + "  Started ago (" + startsElectionDuration + ") Election Id " + v.getStartValidationCycle());
                startElections.setTooltip(new Tooltip("Election Id (" + Utils.toLocal(v.getStartValidationCycle()) + ")"));
            } else {
                startElections.setText(Utils.toLocal(v.getStartElections()) + "  Starts in (" + startsElectionDuration + ")");
                startElections.setTooltip(null);
            }
            long electionDurationInSeconds = v.getEndElections() - v.getStartElections();
            String elections1Duration = DurationFormatUtils.formatDuration(java.time.Duration.ofSeconds(electionDurationInSeconds).toMillis(), "HH:mm:ss", true);

            endElections.setText(Utils.toLocal(v.getEndElections()) + "  Duration (" + elections1Duration + ")");

            long nextElectionsStartsInAgoSeconds = Math.abs(Utils.getCurrentTimeSeconds() - v.getNextElections());
            String nextElectionDuration = DurationFormatUtils.formatDuration(java.time.Duration.ofSeconds(nextElectionsStartsInAgoSeconds).toMillis(), "HH:mm:ss", true);
            if ((Utils.getCurrentTimeSeconds() - v.getNextElections()) > 0) {
                nextElections.setText(Utils.toLocal(v.getNextElections()) + "  Started ago (" + nextElectionDuration + ")");
            } else {
                nextElections.setText(Utils.toLocal(v.getNextElections()) + "  Starts in (" + nextElectionDuration + ")");
            }

            minterAddr.setText(v.getMinterAddr());
            configAddr.setText(v.getConfigAddr());
            electorAddr.setText(v.getElectorAddr());
            validationPeriod.setText(v.getValidationDuration().toString() + " (" + DurationFormatUtils.formatDuration(java.time.Duration.ofSeconds(v.getValidationDuration()).toMillis(), "HH:mm:ss", true) + ")");
            electionPeriod.setText(v.getElectionDuration().toString() + " (" + DurationFormatUtils.formatDuration(java.time.Duration.ofSeconds(v.getElectionDuration()).toMillis(), "HH:mm:ss", true) + ")");

            holdPeriod.setText(v.getHoldPeriod().toString() + " (" + DurationFormatUtils.formatDuration(java.time.Duration.ofSeconds(v.getHoldPeriod()).toMillis(), "HH:mm:ss", true) + ")");
            minimumStake.setText(String.format("%,.9f", new BigDecimal(v.getMinStake().divide(BigInteger.valueOf(ONE_BLN)))));
            maximumStake.setText(String.format("%,.9f", new BigDecimal(v.getMaxStake().divide(BigInteger.valueOf(ONE_BLN)))));

            //every 30 sec
            //MyLocalTonSettings settings = MyLocalTon.getInstance().getSettings();

            AccountState accountState = LiteClientParser.parseGetAccount(liteClient.executeGetAccount(settings.getGenesisNode(), settings.getMainWalletAddrFull()));
            minterBalance.setText(String.format("%,.9f", accountState.getBalance().getToncoins().divide(BigDecimal.valueOf(ONE_BLN), 9, RoundingMode.CEILING)));

            accountState = LiteClientParser.parseGetAccount(liteClient.executeGetAccount(settings.getGenesisNode(), settings.getConfigSmcAddrHex()));
            configBalance.setText(String.format("%,.9f", accountState.getBalance().getToncoins().divide(BigDecimal.valueOf(ONE_BLN), 9, RoundingMode.CEILING)));

            accountState = LiteClientParser.parseGetAccount(liteClient.executeGetAccount(settings.getGenesisNode(), settings.getElectorSmcAddrHex()));
            electorBalance.setText(String.format("%,.9f", accountState.getBalance().getToncoins().divide(BigDecimal.valueOf(ONE_BLN), 9, RoundingMode.CEILING)));

            List<ResultListParticipants> participants = LiteClientParser.parseRunMethodParticipantList(liteClient.executeGetParticipantList(settings.getGenesisNode(), settings.getElectorSmcAddrHex()));
            totalParticipants.setText(String.valueOf(participants.size()));
            String participantsTooltip = "Participants (Public key, weight): " + System.lineSeparator() + LiteClientParser.parseRunMethodParticipantList(liteClient.executeGetParticipantList(settings.getGenesisNode(), settings.getElectorSmcAddrHex())).stream().map(i -> i.getPubkey() + "  " + i.getWeight()).collect(Collectors.joining(System.lineSeparator())) + System.lineSeparator();
            totalParticipants.setTooltip(new Tooltip(participantsTooltip));

            // validator pages
            updateValidator1TabPage(v);
            updateValidator2TabPage(v);
            updateValidator3TabPage(v);
            updateValidator4TabPage(v);
            updateValidator5TabPage(v);
            updateValidator6TabPage(v);
            updateValidator7TabPage(v);

        } catch (Exception e) {
            log.error("Error updating validation tab GUI! Error {}", e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateValidator1TabPage(ValidationParam v) {
        GenesisNode node1 = settings.getGenesisNode();
        if (nonNull(node1.getWalletAddress())) {
            if (isNull(node1.getPrevValidationAndlKey())) { // very first elections, no previous validators yet
                validator1AdnlAddress.setText(v.getCurrentValidators().get(0).getAdnlAddress());
                validator1PubKeyHex.setText(v.getCurrentValidators().get(0).getPublicKey());
                validator1PubKeyInteger.setText(new BigInteger(v.getCurrentValidators().get(0).getPublicKey().toUpperCase(), 16) + " (used in participants list)");
            } else { // in a list of current validators we must find an entry from previous next validators
                for (Validator validator : v.getCurrentValidators()) {
                    if (nonNull(validator.getAdnlAddress())) {
                        if (validator.getAdnlAddress().equals(node1.getPrevValidationAndlKey())) {
                            validator1AdnlAddress.setText(validator.getAdnlAddress());
                            validator1PubKeyHex.setText(validator.getPublicKey());
                            validator1PubKeyInteger.setText(new BigInteger(validator.getPublicKey().toUpperCase(), 16) + " (used in participants list)");
                        }
                    }
                }
            }

            AccountState accountState = LiteClientParser.parseGetAccount(LiteClient.getInstance(LiteClientEnum.GLOBAL).executeGetAccount(settings.getGenesisNode(), node1.getWalletAddress().getFullWalletAddress()));
            validator1AdnlAddressNext.setText(node1.getValidationAndlKey());
            validator1PubKeyHexNext.setText(node1.getValidationPubKeyHex());
            validator1PubKeyIntegerNext.setText(node1.getValidationPubKeyInteger());
            validator1WalletAddress.setText(node1.getWalletAddress().getFullWalletAddress());
            validator1WalletBalance.setText(String.format("%,.9f", accountState.getBalance().getToncoins().divide(BigDecimal.valueOf(ONE_BLN), 9, RoundingMode.CEILING)));
            nodePublicPort1.setText(node1.getPublicPort().toString());
            nodeConsolePort1.setText(node1.getConsolePort().toString());
            liteServerPort1.setText(node1.getLiteServerPort().toString());
        }
    }

    private void updateValidator2TabPage(ValidationParam v) {
        Node2 node2 = settings.getNode2();
        if (nonNull(node2.getWalletAddress())) {
            if (nonNull(node2.getPrevValidationAndlKey())) {
                for (Validator validator : v.getCurrentValidators()) {  // in a list of current validators we must find an entry from previous next validators
                    if (validator.getAdnlAddress().equals(node2.getPrevValidationAndlKey())) {
                        validator2AdnlAddress.setText(validator.getAdnlAddress());
                        validator2PubKeyHex.setText(validator.getPublicKey());
                        validator2PubKeyInteger.setText(new BigInteger(validator.getPublicKey().toUpperCase(), 16) + " (used in participants list)");
                    }
                }
            }

            AccountState accountState = LiteClientParser.parseGetAccount(LiteClient.getInstance(LiteClientEnum.GLOBAL).executeGetAccount(settings.getGenesisNode(), node2.getWalletAddress().getFullWalletAddress()));
            validator2AdnlAddressNext.setText(node2.getValidationAndlKey());
            validator2PubKeyHexNext.setText(node2.getValidationPubKeyHex());
            validator2PubKeyIntegerNext.setText(node2.getValidationPubKeyInteger());
            validator2WalletAddress.setText(node2.getWalletAddress().getFullWalletAddress());
            validator2WalletBalance.setText(String.format("%,.9f", accountState.getBalance().getToncoins().divide(BigDecimal.valueOf(ONE_BLN), 9, RoundingMode.CEILING)));
            nodePublicPort2.setText(node2.getPublicPort().toString());
            nodeConsolePort2.setText(node2.getConsolePort().toString());
            liteServerPort2.setText(node2.getLiteServerPort().toString());
        }
    }

    private void updateValidator3TabPage(ValidationParam v) {
        Node3 node3 = settings.getNode3();
        if (nonNull(node3.getWalletAddress())) {
            if (nonNull(node3.getPrevValidationAndlKey())) {
                for (Validator validator : v.getCurrentValidators()) {
                    if (validator.getAdnlAddress().equals(node3.getPrevValidationAndlKey())) {
                        validator3AdnlAddress.setText(validator.getAdnlAddress());
                        validator3PubKeyHex.setText(validator.getPublicKey());
                        validator3PubKeyInteger.setText(new BigInteger(validator.getPublicKey().toUpperCase(), 16) + " (used in participants list)");
                    }
                }
            }

            AccountState accountState = LiteClientParser.parseGetAccount(LiteClient.getInstance(LiteClientEnum.GLOBAL).executeGetAccount(settings.getGenesisNode(), node3.getWalletAddress().getFullWalletAddress()));
            validator3AdnlAddressNext.setText(node3.getValidationAndlKey());
            validator3PubKeyHexNext.setText(node3.getValidationPubKeyHex());
            validator3PubKeyIntegerNext.setText(node3.getValidationPubKeyInteger());
            validator3WalletAddress.setText(node3.getWalletAddress().getFullWalletAddress());
            validator3WalletBalance.setText(String.format("%,.9f", accountState.getBalance().getToncoins().divide(BigDecimal.valueOf(ONE_BLN), 9, RoundingMode.CEILING)));
            nodePublicPort3.setText(node3.getPublicPort().toString());
            nodeConsolePort3.setText(node3.getConsolePort().toString());
            liteServerPort3.setText(node3.getLiteServerPort().toString());
        }
    }

    private void updateValidator4TabPage(ValidationParam v) {
        Node4 node4 = settings.getNode4();
        if (nonNull(node4.getWalletAddress())) {
            if (nonNull(node4.getPrevValidationAndlKey())) {
                for (Validator validator : v.getCurrentValidators()) {
                    if (validator.getAdnlAddress().equals(node4.getPrevValidationAndlKey())) {
                        validator4AdnlAddress.setText(validator.getAdnlAddress());
                        validator4PubKeyHex.setText(validator.getPublicKey());
                        validator4PubKeyInteger.setText(new BigInteger(validator.getPublicKey().toUpperCase(), 16) + " (used in participants list)");
                    }
                }
            }

            AccountState accountState = LiteClientParser.parseGetAccount(LiteClient.getInstance(LiteClientEnum.GLOBAL).executeGetAccount(settings.getGenesisNode(), node4.getWalletAddress().getFullWalletAddress()));
            validator4AdnlAddressNext.setText(node4.getValidationAndlKey());
            validator4PubKeyHexNext.setText(node4.getValidationPubKeyHex());
            validator4PubKeyIntegerNext.setText(node4.getValidationPubKeyInteger());
            validator4WalletAddress.setText(node4.getWalletAddress().getFullWalletAddress());
            validator4WalletBalance.setText(String.format("%,.9f", accountState.getBalance().getToncoins().divide(BigDecimal.valueOf(ONE_BLN), 9, RoundingMode.CEILING)));
            nodePublicPort4.setText(node4.getPublicPort().toString());
            nodeConsolePort4.setText(node4.getConsolePort().toString());
            liteServerPort4.setText(node4.getLiteServerPort().toString());
        }
    }

    private void updateValidator5TabPage(ValidationParam v) {
        Node5 node5 = settings.getNode5();
        if (nonNull(node5.getWalletAddress())) {
            if (nonNull(node5.getPrevValidationAndlKey())) {
                for (Validator validator : v.getCurrentValidators()) {
                    if (validator.getAdnlAddress().equals(node5.getPrevValidationAndlKey())) {
                        validator5AdnlAddress.setText(validator.getAdnlAddress());
                        validator5PubKeyHex.setText(validator.getPublicKey());
                        validator5PubKeyInteger.setText(new BigInteger(validator.getPublicKey().toUpperCase(), 16) + " (used in participants list)");
                    }
                }
            }

            AccountState accountState = LiteClientParser.parseGetAccount(LiteClient.getInstance(LiteClientEnum.GLOBAL).executeGetAccount(settings.getGenesisNode(), node5.getWalletAddress().getFullWalletAddress()));
            validator5AdnlAddressNext.setText(node5.getValidationAndlKey());
            validator5PubKeyHexNext.setText(node5.getValidationPubKeyHex());
            validator5PubKeyIntegerNext.setText(node5.getValidationPubKeyInteger());
            validator5WalletAddress.setText(node5.getWalletAddress().getFullWalletAddress());
            validator5WalletBalance.setText(String.format("%,.9f", accountState.getBalance().getToncoins().divide(BigDecimal.valueOf(ONE_BLN), 9, RoundingMode.CEILING)));
            nodePublicPort5.setText(node5.getPublicPort().toString());
            nodeConsolePort5.setText(node5.getConsolePort().toString());
            liteServerPort5.setText(node5.getLiteServerPort().toString());
        }
    }

    private void updateValidator6TabPage(ValidationParam v) {
        Node6 node6 = settings.getNode6();
        if (nonNull(node6.getWalletAddress())) {
            if (nonNull(node6.getPrevValidationAndlKey())) {
                for (Validator validator : v.getCurrentValidators()) {
                    if (validator.getAdnlAddress().equals(node6.getPrevValidationAndlKey())) {
                        validator6AdnlAddress.setText(validator.getAdnlAddress());
                        validator6PubKeyHex.setText(validator.getPublicKey());
                        validator6PubKeyInteger.setText(new BigInteger(validator.getPublicKey().toUpperCase(), 16) + " (used in participants list)");
                    }
                }
            }

            AccountState accountState = LiteClientParser.parseGetAccount(LiteClient.getInstance(LiteClientEnum.GLOBAL).executeGetAccount(settings.getGenesisNode(), node6.getWalletAddress().getFullWalletAddress()));
            validator6AdnlAddressNext.setText(node6.getValidationAndlKey());
            validator6PubKeyHexNext.setText(node6.getValidationPubKeyHex());
            validator6PubKeyIntegerNext.setText(node6.getValidationPubKeyInteger() + " (used in participants list)");
            validator6WalletAddress.setText(node6.getWalletAddress().getFullWalletAddress());
            validator6WalletBalance.setText(String.format("%,.9f", accountState.getBalance().getToncoins().divide(BigDecimal.valueOf(ONE_BLN), 9, RoundingMode.CEILING)));
            nodePublicPort6.setText(node6.getPublicPort().toString());
            nodeConsolePort6.setText(node6.getConsolePort().toString());
            liteServerPort6.setText(node6.getLiteServerPort().toString());
        }
    }

    private void updateValidator7TabPage(ValidationParam v) {
        Node7 node7 = settings.getNode7();
        if (nonNull(node7.getWalletAddress())) {
            if (nonNull(node7.getPrevValidationAndlKey())) {
                for (Validator validator : v.getCurrentValidators()) {
                    if (validator.getAdnlAddress().equals(node7.getPrevValidationAndlKey())) {
                        validator7AdnlAddress.setText(validator.getAdnlAddress());
                        validator7PubKeyHex.setText(validator.getPublicKey());
                        validator7PubKeyInteger.setText(new BigInteger(validator.getPublicKey().toUpperCase(), 16) + " (used in participants list)");
                    }
                }
            }

            AccountState accountState = LiteClientParser.parseGetAccount(LiteClient.getInstance(LiteClientEnum.GLOBAL).executeGetAccount(settings.getGenesisNode(), node7.getWalletAddress().getFullWalletAddress()));
            validator7AdnlAddressNext.setText(node7.getValidationAndlKey());
            validator7PubKeyHexNext.setText(node7.getValidationPubKeyHex());
            validator7PubKeyIntegerNext.setText(node7.getValidationPubKeyInteger() + " (used in participants list)");
            validator7WalletAddress.setText(node7.getWalletAddress().getFullWalletAddress());
            validator7WalletBalance.setText(String.format("%,.9f", accountState.getBalance().getToncoins().divide(BigDecimal.valueOf(ONE_BLN), 9, RoundingMode.CEILING)));
            nodePublicPort7.setText(node7.getPublicPort().toString());
            nodeConsolePort7.setText(node7.getConsolePort().toString());
            liteServerPort7.setText(node7.getLiteServerPort().toString());
        }
    }

    private void colorValidationTiming(ValidationParam v) {

        long currentTime = System.currentTimeMillis() / 1000;

        if (v.getStartValidationCycle() > currentTime) {
            startCycle.setTextFill(Color.GREEN);
        } else {
            startCycle.setTextFill(Color.BLACK);
        }

        if (v.getEndValidationCycle() > currentTime) {
            endCycle.setTextFill(Color.GREEN);
        } else {
            endCycle.setTextFill(Color.BLACK);
        }

        if (v.getStartElections() > currentTime) {
            startElections.setTextFill(Color.GREEN);
        } else {
            startElections.setTextFill(Color.BLACK);
        }

        if (v.getEndElections() > currentTime) {
            endElections.setTextFill(Color.GREEN);
        } else {
            endElections.setTextFill(Color.BLACK);
        }

        if (v.getNextElections() > currentTime) {
            nextElections.setTextFill(Color.GREEN);
        } else {
            nextElections.setTextFill(Color.BLACK);
        }
    }

    public void drawElections(ValidationParam v) {
        Platform.runLater(() -> {
            try {
                mainController.drawBarsAndLabels(v); // once in elections enough (bars and labels)
                mainController.updateValidationTabInfo(v);
                mainController.drawTimeLine(v);

                mainController.electionsChartPane.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
                log.error(ExceptionUtils.getStackTrace(e));
            }
        });
    }

    public void drawBarsAndLabels(ValidationParam v) {
        log.info("draw drawBarsAndLabels");
        log.info("size {}, globalSize {}, cycleMod {}, cycleModEqual {}", settings.electionsCounter.size(), settings.electionsCounterGlobal.size(), settings.getCycleMod(), settings.getCycleModEquals());

        try {

            if (settings.electionsCounter.size() > 3) {
                settings.setCycleMod(2);
                settings.setCycleModEquals(0);
                settings.setVeryFirstElections(Boolean.FALSE);
            }

            long electionsDelta = v.getNextElections() - v.getStartElections();
            log.info("currTime - startElections = {} > {} 3*delta", (Utils.getCurrentTimeSeconds() - v.getStartElections()), (electionsDelta * 3));
            // use case when mylocalton started after long pause and electionId was taken last one but the next one is way ahead in time
            if ((Utils.getCurrentTimeSeconds() - v.getStartElections()) > (electionsDelta * 3)) {
                log.info("A. setStartElectionIdEvery3Cycles {}", Utils.toLocal(v.getStartElections()));
                settings.setLastValidationParamEvery3Cycles(v);
            }

            // use case to rotate labels each two elections (and after 3 elections very first time)
            if ((settings.electionsCounter.size() % settings.getCycleMod()) == settings.getCycleModEquals()) {
                log.info("B. setStartElectionIdEvery3Cycles {}", Utils.toLocal(v.getStartElections()));
                settings.setLastValidationParamEvery3Cycles(v);
            }

            positionBars(settings.getLastValidationParamEvery3Cycles());
            addLabelsToBars(settings.getLastValidationParamEvery3Cycles());

            long startXHoldStakeLine3 = (long) stakeHoldRange3.getLayoutX();
            long endHoldStake3 = settings.getStakeHoldRange3End();

            double scaleFactor = (double) 200 / v.getValidationDuration();
            long holdStakeWidth = (long) (v.getHoldPeriod() * scaleFactor);

            log.debug("startXHoldStakeLine3 {}, endHoldStake3 {}, holdStakeWidth {}", startXHoldStakeLine3, endHoldStake3, holdStakeWidth);

            // draw time-line
            long fullWidthInPixels = startXHoldStakeLine3 + holdStakeWidth;
            long fullDurationSeconds = endHoldStake3 - settings.getLastValidationParamEvery3Cycles().getStartElections();
            double scale = (double) fullWidthInPixels / fullDurationSeconds;
            log.debug("full width {}px, {}s, scale {}", fullWidthInPixels, fullDurationSeconds, scale);

            settings.setTimeLineScale(scale);
            saveSettings();

        } catch (Exception e) {
            e.printStackTrace();
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    public void drawTimeLine(ValidationParam v) {

        if (nonNull(settings.getTimeLineScale())) {

            log.info("draw time-line");
            // update time-line position
            long x;
            double xcoord;
            long currentTime = Utils.getCurrentTimeSeconds();

            log.info("Current StartElectionIdEvery3Cycles {}, size {} global {}", Utils.toLocal(settings.getLastValidationParamEvery3Cycles().getStartElections()), settings.electionsCounter.size(), settings.electionsCounterGlobal.size());

            x = currentTime - settings.getLastValidationParamEvery3Cycles().getStartElections();
            double scaleFactor = (double) 200 / v.getValidationDuration();
            long electionsDelta = v.getNextElections() - v.getStartElections();
            long electionsDeltaWidth = (long) (electionsDelta * scaleFactor);

            if (settings.electionsCounter.size() > 3) {
                xcoord = electionsDeltaWidth + (x * settings.getTimeLineScale());
            } else {
                xcoord = 0 + (x * settings.getTimeLineScale());
            }
            log.debug("electionsDelta {}, electionsDeltaWidth {}, xcoord {}", electionsDelta, electionsDeltaWidth, xcoord);
            timeLine.setLayoutX(xcoord);
            saveSettings();
        }
    }

    private void positionBars(ValidationParam v) {
        // assume duration of validation cycle is 1 unit of 200 pixels, then other ranges scaled down/up accordingly
        double scaleFactor = (double) 200 / v.getValidationDuration();

        long space = 3;
        long validationWidth = 200;
        long electionsWidth = (long) ((v.getEndElections() - v.getStartElections()) * scaleFactor);
        long pauseWidth = (long) ((v.getStartValidationCycle() - v.getEndElections()) * scaleFactor);
        long holdStakeWidth = (long) (v.getHoldPeriod() * scaleFactor);
        long electionsDelta = v.getNextElections() - v.getStartElections();
        long electionsDeltaWidth = (long) (electionsDelta * scaleFactor);
        log.debug("elections delta {}s, {}px", electionsDelta, electionsDeltaWidth);

        // start X position of line 1 (very first elections)
        long startXElectionsLine1 = 0;
        long startXPauseLine1 = startXElectionsLine1 + electionsWidth + space;
        long startXValidationLine1 = startXPauseLine1 + pauseWidth + space;
        long startXHoldStakeLine1 = startXValidationLine1 + validationWidth + space;

        // start X position of line 2 (next elections)
        long startXElectionsLine2 = (long) ((startXValidationLine1 + validationWidth) - (scaleFactor * v.getStartElectionsBefore())) - 3;
        long startXPauseLine2 = startXElectionsLine2 + electionsWidth + space;
        long startXValidationLine2 = startXPauseLine2 + pauseWidth + space;
        long startXHoldStakeLine2 = startXValidationLine2 + validationWidth + space;

        // start X position of line 3 (next elections)
        long startXElectionsLine3 = startXElectionsLine2 + startXElectionsLine2 - startXElectionsLine1;
        long startXPauseLine3 = startXElectionsLine3 + electionsWidth + space;
        long startXValidationLine3 = startXPauseLine3 + pauseWidth + space;
        long startXHoldStakeLine3 = startXValidationLine3 + validationWidth + space;

        log.debug("electionsWidth {}, pauseWidth {}, validationWidth {}, hostStakeWidth {}", electionsWidth, pauseWidth, validationWidth, holdStakeWidth);

        electionsRange1.setMinWidth(electionsWidth);
        electionsRange2.setMinWidth(electionsWidth);
        electionsRange3.setMinWidth(electionsWidth);

        pauseRange1.setMinWidth(pauseWidth);
        pauseRange2.setMinWidth(pauseWidth);
        pauseRange3.setMinWidth(pauseWidth);

        validationRange1.setMinWidth(validationWidth);
        validationRange2.setMinWidth(validationWidth);
        validationRange3.setMinWidth(validationWidth);

        stakeHoldRange1.setMinWidth(holdStakeWidth);
        stakeHoldRange2.setMinWidth(holdStakeWidth);
        stakeHoldRange3.setMinWidth(holdStakeWidth);

        electionsRange1.setLayoutX(startXElectionsLine1);
        pauseRange1.setLayoutX(startXPauseLine1);
        validationRange1.setLayoutX(startXValidationLine1);
        stakeHoldRange1.setLayoutX(startXHoldStakeLine1);

        electionsRange2.setLayoutX(startXElectionsLine2);
        pauseRange2.setLayoutX(startXPauseLine2);
        validationRange2.setLayoutX(startXValidationLine2);
        stakeHoldRange2.setLayoutX(startXHoldStakeLine2);

        electionsRange3.setLayoutX(startXElectionsLine3);
        pauseRange3.setLayoutX(startXPauseLine3);
        validationRange3.setLayoutX(startXValidationLine3);
        stakeHoldRange3.setLayoutX(startXHoldStakeLine3);

    }

    private void addLabelsToBars(ValidationParam v) {
        long electionsDelta = v.getNextElections() - v.getStartElections();
        long electionDurationInSeconds = v.getEndElections() - v.getStartElections();
        String elections1Duration;
        String elections1ToolTip;
        long pauseDurationInSeconds;
        String pause1Duration;
        long validationDurationInSeconds;
        String validation1Duration;
        long stakeHoldDurationInSeconds;
        String stakeHold1Duration;

        log.debug("0.addLabelsToBars, size {}", settings.electionsCounter.size());
        //1
        elections1Duration = DurationFormatUtils.formatDuration(java.time.Duration.ofSeconds(electionDurationInSeconds).toMillis(), "HH:mm:ss", true);
        elections1ToolTip = String.format("Start: %s%nEnd: %s%nDuration: %s", Utils.toLocal(v.getStartElections()), Utils.toLocal(v.getEndElections()), elections1Duration);
        if (settings.electionsCounter.size() <= 3) {
            log.debug("1.addLabelsToBars, size {}, globalSize {}", settings.electionsCounter.size(), settings.electionsCounterGlobal.size());
            electionsRange1.setTooltip(new Tooltip(elections1ToolTip));
        } else {
            log.debug("2.addLabelsToBars, size {}, globalSize {}", settings.electionsCounter.size(), settings.electionsCounterGlobal.size());
            String elections1Duration0 = DurationFormatUtils.formatDuration(java.time.Duration.ofSeconds(electionDurationInSeconds).toMillis(), "HH:mm:ss", true);
            String elections1ToolTip0 = String.format("Start: %s%nEnd: %s%nDuration: %s", Utils.toLocal(v.getStartElections() - electionsDelta), Utils.toLocal(v.getEndElections() - electionsDelta), elections1Duration0);
            electionsRange1.setTooltip(new Tooltip(elections1ToolTip0));
        }

        pauseDurationInSeconds = v.getStartValidationCycle() - v.getEndElections();
        pause1Duration = DurationFormatUtils.formatDuration(java.time.Duration.ofSeconds(pauseDurationInSeconds).toMillis(), "HH:mm:ss", true);
        String pause1ToolTip = String.format("Start: %s%nEnd: %s%nDuration: %s", Utils.toLocal(v.getEndElections()), Utils.toLocal(v.getStartValidationCycle()), pause1Duration);
        if (settings.electionsCounter.size() <= 3) {
            pauseRange1.setTooltip(new Tooltip(pause1ToolTip));
        } else {
            String pause1Duration0 = DurationFormatUtils.formatDuration(java.time.Duration.ofSeconds(pauseDurationInSeconds).toMillis(), "HH:mm:ss", true);
            String pause1ToolTip0 = String.format("Start: %s%nEnd: %s%nDuration: %s", Utils.toLocal(v.getEndElections() - electionsDelta), Utils.toLocal(v.getStartValidationCycle() - electionsDelta), pause1Duration0);
            pauseRange1.setTooltip(new Tooltip(pause1ToolTip0));
        }

        validationDurationInSeconds = v.getEndValidationCycle() - v.getStartValidationCycle();
        validation1Duration = DurationFormatUtils.formatDuration(java.time.Duration.ofSeconds(validationDurationInSeconds).toMillis(), "HH:mm:ss", true);
        String validation1ToolTip = String.format("Start: %s%nEnd: %s%nDuration: %s", Utils.toLocal(v.getStartValidationCycle()), Utils.toLocal(v.getEndValidationCycle()), validation1Duration);
        if (settings.electionsCounter.size() <= 3) {
            validationRange1.setTooltip(new Tooltip(validation1ToolTip));
        } else {
            String validation1Duration0 = DurationFormatUtils.formatDuration(java.time.Duration.ofSeconds(validationDurationInSeconds).toMillis(), "HH:mm:ss", true);
            String validation1ToolTip0 = String.format("Start: %s%nEnd: %s%nDuration: %s", Utils.toLocal(v.getStartValidationCycle() - electionsDelta), Utils.toLocal(v.getEndValidationCycle() - electionsDelta), validation1Duration0);
            validationRange1.setTooltip(new Tooltip(validation1ToolTip0));
        }

        stakeHoldDurationInSeconds = v.getHoldPeriod();
        stakeHold1Duration = DurationFormatUtils.formatDuration(java.time.Duration.ofSeconds(stakeHoldDurationInSeconds).toMillis(), "HH:mm:ss", true);
        String holdStake1ToolTip = String.format("Start: %s%nEnd: %s%nDuration: %s", Utils.toLocal(v.getEndValidationCycle()), Utils.toLocal(java.time.Duration.ofSeconds(v.getEndValidationCycle()).plusSeconds(v.getHoldPeriod()).toSeconds()), stakeHold1Duration);
        if (settings.electionsCounter.size() <= 3) {
            stakeHoldRange1.setTooltip(new Tooltip(holdStake1ToolTip));
        } else {
            String stakeHold1Duration0 = DurationFormatUtils.formatDuration(java.time.Duration.ofSeconds(stakeHoldDurationInSeconds).toMillis(), "HH:mm:ss", true);
            String holdStake1ToolTip0 = String.format("Start: %s%nEnd: %s%nDuration: %s", Utils.toLocal(v.getEndValidationCycle() - electionsDelta), Utils.toLocal(java.time.Duration.ofSeconds(v.getEndValidationCycle() - electionsDelta).plusSeconds(v.getHoldPeriod()).toSeconds()), stakeHold1Duration0);
            stakeHoldRange1.setTooltip(new Tooltip(holdStake1ToolTip0));
        }

        //2
        long endElections2 = java.time.Duration.ofSeconds(v.getNextElections()).plusSeconds(electionDurationInSeconds).toSeconds();
        String elections2ToolTip = String.format("Start: %s%nEnd: %s%nDuration: %s", Utils.toLocal(v.getNextElections()), Utils.toLocal(endElections2), elections1Duration);
        if (settings.electionsCounter.size() <= 3) {
            electionsRange2.setTooltip(new Tooltip(elections2ToolTip));
        } else {
            electionsRange2.setTooltip(new Tooltip(elections1ToolTip));
        }

        long endPause2 = java.time.Duration.ofSeconds(endElections2).plusSeconds(pauseDurationInSeconds).toSeconds();
        String pause2ToolTip = String.format("Start: %s%nEnd: %s%nDuration: %s", Utils.toLocal(endElections2), Utils.toLocal(endPause2), pause1Duration);
        if (settings.electionsCounter.size() <= 3) {
            pauseRange2.setTooltip(new Tooltip(pause2ToolTip));
        } else {
            pauseRange2.setTooltip(new Tooltip(pause1ToolTip));
        }

        long endValidation2 = java.time.Duration.ofSeconds(endPause2).plusSeconds(validationDurationInSeconds).toSeconds();
        String validation2ToolTip = String.format("Start: %s%nEnd: %s%nDuration: %s", Utils.toLocal(endPause2), Utils.toLocal(endValidation2), validation1Duration);
        if (settings.electionsCounter.size() <= 3) {
            validationRange2.setTooltip(new Tooltip(validation2ToolTip));
        } else {
            validationRange2.setTooltip(new Tooltip(validation1ToolTip));
        }

        long endHoldStake2 = java.time.Duration.ofSeconds(endValidation2).plusSeconds(v.getHoldPeriod()).toSeconds();
        String holdStake2ToolTip = String.format("Start: %s%nEnd: %s%nDuration: %s", Utils.toLocal(endValidation2), Utils.toLocal(endHoldStake2), stakeHold1Duration);
        if (settings.electionsCounter.size() <= 3) {
            stakeHoldRange2.setTooltip(new Tooltip(holdStake2ToolTip));
        } else {
            stakeHoldRange2.setTooltip(new Tooltip(holdStake1ToolTip));
        }

        //3
        long startElections3 = java.time.Duration.ofSeconds(v.getNextElections()).plusSeconds(electionsDelta).toSeconds();
        long endElections3 = java.time.Duration.ofSeconds(v.getNextElections()).plusSeconds(electionDurationInSeconds + electionsDelta).toSeconds();
        String elections3ToolTip = String.format("Start: %s%nEnd: %s%nDuration: %s", Utils.toLocal(startElections3), Utils.toLocal(endElections3), elections1Duration);
        if (settings.electionsCounter.size() <= 3) {
            electionsRange3.setTooltip(new Tooltip(elections3ToolTip));
        } else {
            electionsRange3.setTooltip(new Tooltip(elections2ToolTip));
        }

        long endPause3 = java.time.Duration.ofSeconds(endElections3).plusSeconds(pauseDurationInSeconds).toSeconds();
        String pause3ToolTip = String.format("Start: %s%nEnd: %s%nDuration: %s", Utils.toLocal(endElections3), Utils.toLocal(endPause3), pause1Duration);
        if (settings.electionsCounter.size() <= 3) {
            pauseRange3.setTooltip(new Tooltip(pause3ToolTip));
        } else {
            pauseRange3.setTooltip(new Tooltip(pause2ToolTip));
        }

        long endValidation3 = java.time.Duration.ofSeconds(endPause3).plusSeconds(validationDurationInSeconds).toSeconds();
        String validation3ToolTip = String.format("Start: %s%nEnd: %s%nDuration: %s", Utils.toLocal(endPause3), Utils.toLocal(endValidation3), validation1Duration);
        if (settings.electionsCounter.size() <= 3) {
            validationRange3.setTooltip(new Tooltip(validation3ToolTip));
        } else {
            validationRange3.setTooltip(new Tooltip(validation2ToolTip));
        }

        long endHoldStake3 = java.time.Duration.ofSeconds(endValidation3).plusSeconds(v.getHoldPeriod()).toSeconds();
        String holdStake3ToolTip = String.format("Start: %s%nEnd: %s%nDuration: %s", Utils.toLocal(endValidation3), Utils.toLocal(endHoldStake3), stakeHold1Duration);
        if (settings.electionsCounter.size() <= 3) {
            stakeHoldRange3.setTooltip(new Tooltip(holdStake3ToolTip));
            settings.setStakeHoldRange3End(endHoldStake3);
        } else {
            stakeHoldRange3.setTooltip(new Tooltip(holdStake2ToolTip));
            settings.setStakeHoldRange3End(endHoldStake3);
        }
    }

    public void validation1AdnlClicked(MouseEvent mouseEvent) {
        String addr = validator1AdnlAddress.getText();
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(addr);
        clipboard.setContent(content);
        log.debug(addr + " copied");
        App.mainController.showInfoMsg(addr + " copied to clipboard", 0.5);
        mouseEvent.consume();
    }

    public void validation1WalletAddrClicked(MouseEvent mouseEvent) {
        String addr = validator1WalletAddress.getText();
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(addr);
        clipboard.setContent(content);
        log.debug(addr + " copied");
        App.mainController.showInfoMsg(addr + " copied to clipboard", 0.5);
        mouseEvent.consume();
    }

    public void validation1PubKeyIntegerClicked(MouseEvent mouseEvent) {
        String addr = validator1PubKeyInteger.getText();
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(addr);
        clipboard.setContent(content);
        log.debug(addr + " copied");
        App.mainController.showInfoMsg(addr + " copied to clipboard", 0.5);
        mouseEvent.consume();
    }

    public void validation1PubKeyHexClicked(MouseEvent mouseEvent) {
        String addr = validator1PubKeyHex.getText();
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(addr);
        clipboard.setContent(content);
        log.debug(addr + " copied");
        App.mainController.showInfoMsg(addr + " copied to clipboard", 0.5);
        mouseEvent.consume();
    }

    public void validation1AdnlClickedNext(MouseEvent mouseEvent) {
        String addr = validator1AdnlAddressNext.getText();
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(addr);
        clipboard.setContent(content);
        log.debug(addr + " copied");
        App.mainController.showInfoMsg(addr + " copied to clipboard", 0.5);
        mouseEvent.consume();
    }

    public void validation1PubKeyHexClickedNext(MouseEvent mouseEvent) {
        String addr = validator1PubKeyHexNext.getText();
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(addr);
        clipboard.setContent(content);
        log.debug(addr + " copied");
        App.mainController.showInfoMsg(addr + " copied to clipboard", 0.5);
        mouseEvent.consume();
    }

    public void validation1PubKeyIntegerClickedNext(MouseEvent mouseEvent) {
        String addr = validator1PubKeyIntegerNext.getText();
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(addr);
        clipboard.setContent(content);
        log.debug(addr + " copied");
        App.mainController.showInfoMsg(addr + " copied to clipboard", 0.5);
        mouseEvent.consume();
    }

    public void copyElectionId(MouseEvent mouseEvent) {
        String electionId = startElections.getText();
        electionId = electionId.substring(electionId.indexOf("Election Id ") + 12);
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(electionId);
        clipboard.setContent(content);
        log.debug(electionId + " copied");
        App.mainController.showInfoMsg(electionId + " copied to clipboard", 0.5);
        mouseEvent.consume();
    }

    public void createNewNodeBtn() {

        ExecutorService newNodeExecutorService = Executors.newSingleThreadExecutor();

        newNodeExecutorService.execute(() -> {
            Thread.currentThread().setName("MyLocalTon - Creating validator");
//            if (Long.parseLong(mainController.currentBlockNum.getText()) < 120) {
//                showDialogMessage("Too early", "Please wait for 120 blocks to be generated. At least one validator group should be rotated.");
//            return;
//            }
            try {
                mainController.addValidatorBtn.setDisable(true);

                org.ton.settings.Node node = Utils.getNewNode();
                if (nonNull(node)) {
                    log.info("creating validator {}", node.getNodeName());
                    App.mainController.showInfoMsg("Creating validator " + node.getNodeName() + ". You will be informed once it's finished.", 8);
                    Thread.sleep(500);

                    //delete unfinished node creation
                    FileUtils.deleteQuietly(new File(MyLocalTonSettings.MY_APP_DIR + File.separator + node.getNodeName()));

                    MyLocalTon.getInstance().createFullnode(node, true, true);

                    if (isWindows()) {
                        Utils.waitForBlockchainReady(node);
                        Utils.waitForNodeSynchronized(node);
                    }

                    Tab newTab = Utils.getNewNodeTab();
                    Platform.runLater(() -> {
                        validationTabs.getTabs().add(newTab);
                    });

                    settings.getActiveNodes().add(node.getNodeName());
                    MyLocalTon.getInstance().saveSettingsToGson();
                    mainController.addValidatorBtn.setDisable(false);

                    // FYI. Status of all nodes reported back from the thread "Node Monitor" and shown on a corresponding tab

                    //App.mainController.showInfoMsg("Validator " + node.getNodeName() + " has been successfully created", 5);
                    showDialogMessage("Completed", "Validator " + node.getNodeName() + " has been successfully created, now synchronizing. Once elections will be opened it will take part in them.");
                } else {
                    showDialogMessage("The limit has been reached", "It is possible to have up to 6 additional validators. The first one is reserved, thus in total you may have 7 validators.");
                }
            } catch (Exception e) {
                log.error("Error creating validator: {}", e.getMessage());
                App.mainController.showErrorMsg("Error creating validator", 3);
            } finally {
                mainController.addValidatorBtn.setDisable(false);
            }

        });
        newNodeExecutorService.shutdown();
    }

    public void deleteValidator2Btn() {
        log.info("delete 2 validator");
        showDialogConfirmDeleteNode(settings.getNode2());
    }

    public void deleteValidator3Btn() {
        log.info("delete 3 validator");
        showDialogConfirmDeleteNode(settings.getNode3());
    }

    public void deleteValidator4Btn() {
        log.info("delete 4 validator");
        showDialogConfirmDeleteNode(settings.getNode4());
    }

    public void deleteValidator5Btn() {
        log.info("delete 5 validator");
        showDialogConfirmDeleteNode(settings.getNode5());
    }

    public void deleteValidator6Btn() {
        log.info("delete 6 validator");
        showDialogConfirmDeleteNode(settings.getNode6());
    }

    public void deleteValidator7Btn() {
        log.info("delete 7 validator");
        showDialogConfirmDeleteNode(settings.getNode7());
    }

    public void valLogDirBtnAction2() throws IOException {
        log.info("open validator log dir {}", validatorLogDir2.getText().trim());
        if (isWindows()) {
            Runtime.getRuntime().exec("cmd /c start " + validatorLogDir2.getText());
        } else {
            Runtime.getRuntime().exec("gio open " + validatorLogDir2.getText());
        }
    }

    public void valLogDirBtnAction3() throws IOException {
        log.debug("open validator log dir {}", validatorLogDir3.getText().trim());
        if (isWindows()) {
            Runtime.getRuntime().exec("cmd /c start " + validatorLogDir3.getText());
        } else {
            Runtime.getRuntime().exec("gio open " + validatorLogDir3.getText());
        }
    }

    public void valLogDirBtnAction4() throws IOException {
        log.debug("open validator log dir {}", validatorLogDir4.getText().trim());
        if (isWindows()) {
            Runtime.getRuntime().exec("cmd /c start " + validatorLogDir4.getText());
        } else {
            Runtime.getRuntime().exec("gio open " + validatorLogDir4.getText());
        }
    }

    public void valLogDirBtnAction5() throws IOException {
        log.debug("open validator log dir {}", validatorLogDir5.getText().trim());
        if (isWindows()) {
            Runtime.getRuntime().exec("cmd /c start " + validatorLogDir5.getText());
        } else {
            Runtime.getRuntime().exec("gio open " + validatorLogDir5.getText());
        }
    }

    public void valLogDirBtnAction6() throws IOException {
        log.debug("open validator log dir {}", validatorLogDir6.getText().trim());
        if (isWindows()) {
            Runtime.getRuntime().exec("cmd /c start " + validatorLogDir6.getText());
        } else {
            Runtime.getRuntime().exec("gio open " + validatorLogDir6.getText());
        }
    }

    public void valLogDirBtnAction7() throws IOException {
        log.debug("open validator log dir {}", validatorLogDir7.getText().trim());
        if (isWindows()) {
            Runtime.getRuntime().exec("cmd /c start " + validatorLogDir7.getText());
        } else {
            Runtime.getRuntime().exec("gio open " + validatorLogDir7.getText());
        }
    }

    public void copyDonationTonAddress(MouseEvent mouseEvent) {
        String addr = tonDonationAddress.getText();
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(addr);
        clipboard.setContent(content);
        log.debug(addr + " copied");
        App.mainController.showInfoMsg(addr + " copied to clipboard", 1);
        mouseEvent.consume();
    }
}
