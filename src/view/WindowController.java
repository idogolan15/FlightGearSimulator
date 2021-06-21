package view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.XYChart;
import view.buttons.MyButtons;
import view.graphs.MyGraphs;
import view.joystick.MyJoystick;
import view.openfiles.OpenFiles;
import view.featuresList.MyFeaturesList;
import viewModel.ViewModel;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class WindowController implements Initializable {

    @FXML
    MyJoystick myJoystick;

    @FXML
    MyFeaturesList viewlist;

    @FXML MyGraphs myGraphs;

    @FXML MyButtons myButtons;

    @FXML OpenFiles openFiles;

    ViewModel vm;


    private StringProperty selectedName;
    private XYChart.Series seriesPointA;
    private XYChart.Series seriesPointB;
    private XYChart.Series seriesPointAnomaly;
    private XYChart.Series seriesTimeAnomaly;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }




    @SuppressWarnings("unchecked")
    public void init(ViewModel vm2) {
        // TODO Auto-generated method stub
        this.vm = vm2;
        MyFeaturesList.xmlpath.addListener((o, ov, nv)->{
            this.vm.loadXml(nv);
            if (vm.getXs() != null) {
                double maxR = this.vm.getXs().getSetting("rudder").getMax();
                double minR = this.vm.getXs().getSetting("rudder").getMin();
                double maxT = this.vm.getXs().getSetting("throttle").getMax();
                double minT = this.vm.getXs().getSetting("throttle").getMin();
                myJoystick.SetMaxMinForSliders(maxR, minR, maxT, minT);
            }
        });


        selectedName=new SimpleStringProperty("");

        MyFeaturesList.list.getSelectionModel().selectedItemProperty().addListener((o, ov, nv)->{
            String selected=   MyFeaturesList.list.getSelectionModel().getSelectedItem().toString();
            selectedName.setValue(selected);
        });





        openFiles.csvpath.addListener((o,ov,nv)->{
            this.vm.loadCsv(nv);
            ArrayList<String> titles =  this.vm.getColTitles();
            if (titles != null) {
                ObservableList<String> list = FXCollections.observableArrayList(titles);
                viewlist.list.setItems(list);
            }

        });

        openFiles.algoname.addListener((o,ov,nv)->{
            this.vm.loadAnomalyAlgo(openFiles.algopath.get(), nv);
            if (this.vm.getAd() !=null) {
                myGraphs.Bchart.setTitle(nv.substring(11));
            }
        });

        myButtons.forwardCnt.addListener((o,ov,nv)->{
            vm.Forward1();
        });
        myButtons.forward2Cnt.addListener((o,ov,nv)->{
            vm.Forward2();
        });

        myButtons.backwardCnt.addListener((o,ov,nv)->{
            vm.Backward1();
        });

        myButtons.backward2Cnt.addListener((o,ov,nv)->{
            vm.Backward2();
        });

        myButtons.videoSlider.addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldValue,
                    Number newValue) {
                String currTime = toStringTime(myButtons.videoSlider.doubleValue());
                myButtons.VideoTime.textProperty().setValue(currTime);
            }

        });

        myButtons.myButtonsController.getSlider().setOnMouseReleased(e -> {
            vm.setTimeStemp((int)myButtons.myButtonsController.getSlider().getValue());
        });



        vm.rate.bindBidirectional(myButtons.videoSpeed.valueProperty());
        myButtons.videoSpeed.valueProperty().addListener((o,ov,nv)->{
            myButtons.videoSpeed.setValue(nv);
        });
        myButtons.FlightStatus.bindBidirectional(vm.FlightStatus);
        myButtons.FlightGear.textProperty().bind(vm.FlightMessage);
        myButtons.FlightStatus.addListener((o,ov,nv)->{
            if (((String)nv).equals("Fly")) {
                this.vm.StartFlight(0);
            }
            else if (((String)nv).equals("not Fly")) {
                this.vm.stopFlight();
                this.vm.setTime(0);
            }
            else if (((String)nv).equals("pause Fly")) {
                int currentTime = this.vm.getTime();
                this.vm.pauseFlight();
                this.vm.setTime(currentTime);
            }
        });

        myJoystick.aileron.bind(this.vm.aileron);
        myJoystick.aileron.addListener((o,ov,nv)->{
            double maxA = this.vm.getXs().getSetting("aileron").getMax();
            double minA = this.vm.getXs().getSetting("aileron").getMin();
            double a=myJoystick.bigCircle.getLayoutX()-myJoystick.bigCircle.getRadius();
            double b=myJoystick.bigCircle.getLayoutX()+myJoystick.bigCircle.getRadius();
            nv=myJoystick.NormlaizeJoystic((double)nv ,maxA,minA,a,b);
            myJoystick.joyCircle.setLayoutX((double) nv);
        });
        myJoystick.elevators.bind(this.vm.elevators);
        myJoystick.elevators.addListener((o,ov,nv)->{
            double maxE = this.vm.getXs().getSetting("elevator").getMax();
            double minE = this.vm.getXs().getSetting("elevator").getMin();
            double a=myJoystick.bigCircle.getLayoutY()-myJoystick.bigCircle.getRadius();
            double b=myJoystick.bigCircle.getLayoutY()+myJoystick.bigCircle.getRadius();
            nv=myJoystick.NormlaizeJoystic((double)nv ,maxE,minE,a,b);
            myJoystick.joyCircle.setLayoutY((double) nv);
        });

        myJoystick.rudder.bind(this.vm.rudder);
        myJoystick.throttle.bind(this.vm.throttle);

        myJoystick.AltitudeValue.textProperty().bind(this.vm.height);
        myJoystick.DirectionValue.textProperty().bind(this.vm.direction);
        myJoystick.PitchValue.textProperty().bind(this.vm.pitch);
        myJoystick.RollValue.textProperty().bind(this.vm.roll);
        myJoystick.speedValue.textProperty().bind(this.vm.speed);
        myJoystick.yawValue.textProperty().bind(this.vm.yaw);
        myButtons.videoSlider.bindBidirectional(this.vm.videoslider);


        seriesPointA= new XYChart.Series();
        seriesPointB= new XYChart.Series();
        seriesPointAnomaly= new XYChart.Series();
        seriesTimeAnomaly= new XYChart.Series();

        myGraphs.Fchart.getData().add(seriesPointA);
        myGraphs.CorChart.getData().add(seriesPointB);
        myGraphs.Bchart.getData().addAll(seriesPointAnomaly,seriesTimeAnomaly);

        selectedName.addListener((o,nv,ov)->{
            if((!(selectedName.getValue().equals("")))&&nv.equals(ov)){
                this.vm.getModel().addValueAtTime(selectedName.getValue(),seriesPointA);
            }
            else if(!nv.equals(ov)){
                this.vm.getModel().addValueUntilTime(selectedName.getValue(),seriesPointA);
            }
        });

        this.vm.videoslider.addListener(v->{
            if(!selectedName.getValue().equals("")){
                this.vm.getModel().addValueAtTime(selectedName.getValue(),seriesPointA);
                if(!(openFiles.algoname.getValue().equals("Zscore"))){
                    String fCor=this.vm.getModel().FindCorrelative(selectedName.getValue(),openFiles.algoname.getValue());
                    if(fCor!=null) {
                        this.vm.getModel().addValueAtTime(fCor, seriesPointB);
                    }
                }
            }
//            if(!(openFiles.algoname.getValue().equals("Zscore"))){
//                this.vm.getModel().addValueAtTime(this.vm.getModel().FindCorrelative(selectedName.getValue(),openFiles.algoname.getValue()),seriesPointB);
//            }
        });







    }

    public String toStringTime(Double object) {
        long seconds = object.longValue();
        long minutes = TimeUnit.SECONDS.toMinutes(seconds);
        long remainingseconds = seconds - TimeUnit.MINUTES.toSeconds(minutes);
        return String.format("%02d", minutes) + ":" + String.format("%02d", remainingseconds);
    }
}