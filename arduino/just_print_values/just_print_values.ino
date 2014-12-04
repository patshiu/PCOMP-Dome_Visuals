long lowestValue; 
long highestValue;
long difference; 
long averageValue; 
int timer; 

void setup() {
  Serial.begin(9600);
}

void loop() {
  int sensorValue = analogRead(A0);
  Serial.println(sensorValue);
  delay(2);
  //detectRange();
}

/*void detectRange() {
  //Serial.println(F("THIS IS RUNNING"));
  //Spend a set amount of time reading the sensor
  //return the lowest and highest values
  //so that we can take action depending on 
  //1. the median value
  //2. the difference between peak and trough 
  
  timer = 5000; //set timer up
  long valuesArray[timer];
  //Reset the min and max values
  lowestValue = 0; 
  highestValue = 0;
  difference = 0; 
  averageValue = 0; 
  
  while( timer >= 0 ){
    timer--; 
    valuesArray[timer] = analogRead(A0)/4;
    
    long sensorValue = analogRead(A0)/4;
    
    lowestValue = min(lowestValue, sensorValue);
    highestValue = max(highestValue, sensorValue);
  }
    difference = highestValue - lowestValue; 
    long sum; 
    for (long i = 0; i < timer-1; i++){
      sum = sum + valuesArray[i];
    }
    averageValue = sum / timer; 
    
    //PRINT THE LABELS
    Serial.print(F("Lowest value : "));
    Serial.print("\t");
    
    Serial.print(F("Highest value : "));
    Serial.print("\t");
    
    Serial.print(F("Variance : "));
    Serial.print("\t");
    
    Serial.print(F("Mean : "));
    Serial.println("\t");  
    
    
    //PRINT THE VALUES
    Serial.print(lowestValue, DEC);
    Serial.print("\t");
    
    Serial.print(highestValue, DEC);
    Serial.print("\t");
    
    Serial.print(difference, DEC);
    Serial.print("\t");
    
    Serial.print(averageValue, DEC);
    Serial.println("\t");  
    Serial.println(""); 
}*/
