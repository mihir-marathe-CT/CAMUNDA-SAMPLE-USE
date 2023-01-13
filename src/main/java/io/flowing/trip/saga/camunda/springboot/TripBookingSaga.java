package io.flowing.trip.saga.camunda.springboot;

import io.flowing.trip.saga.camunda.springboot.conf.TrackExecutionTime;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import java.util.Map;
import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.camunda.bpm.engine.ProcessEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.flowing.trip.saga.camunda.adapter.BookFlightAdapter;
import io.flowing.trip.saga.camunda.adapter.BookHotelAdapter;
import io.flowing.trip.saga.camunda.adapter.CancelCarAdapter;
import io.flowing.trip.saga.camunda.adapter.CancelFlightAdapter;
import io.flowing.trip.saga.camunda.adapter.CancelHotelAdapter;
import io.flowing.trip.saga.camunda.adapter.ReserveCarAdapter;
import io.flowing.trip.saga.camunda.springboot.builder.SagaBuilder;

@Component
//@Singleton
@Slf4j
public class TripBookingSaga {

  @Autowired
  private ProcessEngine camunda;

  private static final Logger logs = LogManager.getLogger(TripBookingSaga.class);
  @PostConstruct
  public void defineSaga() {
    SagaBuilder saga = SagaBuilder.newSaga("trip") //
        .activity("Reserve car", ReserveCarAdapter.class) //
        .compensationActivity("Cancel car", CancelCarAdapter.class) //
        .activity("Book hotel", BookHotelAdapter.class) //
        .compensationActivity("Cancel hotel", CancelHotelAdapter.class) //
        .activity("Book flight", BookFlightAdapter.class) //
        .compensationActivity("Cancel flight", CancelFlightAdapter.class) //
        .end() //
        .triggerCompensationOnAnyError();

    camunda.getRepositoryService().createDeployment() //
        .addModelInstance("trip.bpmn", saga.getModel()) //
        .deploy();

//    File file = new File("result.bpmn");
//    Bpmn.writeModelToFile(file, saga.getModel());
  }

  @TrackExecutionTime
  public void bookTrip() {
    HashMap<String, Object> someVariables = new HashMap<>();

    for (int i =0;i<=50;i++){
      HashMap<String, Object> someVariables1 = new HashMap<>();

      someVariables.put("dump"+i,"ramdom");
//      someVariables.put("dumMap"+i,someVariables1);
    }
    size(someVariables);
    System.out.println(someVariables.size());
    // Could add some variables here - not used in simple demo
    camunda.getRuntimeService().startProcessInstanceByKey("trip", someVariables);
  }

  public static void size(Map<String, Object> map) {
    try{
      System.out.println("Index Size: " + map.size());
      ByteArrayOutputStream baos=new ByteArrayOutputStream();
      ObjectOutputStream oos=new ObjectOutputStream(baos);
      oos.writeObject(map);
      oos.close();
      System.out.println("Data Size: " + baos.size());
    }catch(IOException e){
      e.printStackTrace();
    }
  }

}
