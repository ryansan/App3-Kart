<?php
$con=mysqli_connect('student.cs.hioa.no','s326149','','s326149');

$times = array(1575158400, 1575244800, 1575331200, 1575417600, 1575504000, 1575590400, 1575676800);


for($i = 0; $i < count($times); $i++){
  
  $orderDate = $times[$i];
  $date = date("Y-m-d", substr($orderDate, 0, 10));
  $fromHour= 7;
  $toHour= 10;
  $name = "Ryan";
  $description = "Øving med gruppa";
  $mail = "ryan@mail.com";
  $room_ID = 43; 
  
  $sql=mysqli_query($con,"insert into Orders(OrderDate, FromHour, ToHour, Name, Description, Mail, Room_ID) values('$date','$fromHour','$toHour','$name','$description','$mail','$room_ID');");
 
  if($sql){
    echo "post order ok";
  }else{
    die(mysql_error());
  }
  
}

  for($i = 0; $i < count($times); $i++){
  
  $orderDate = $times[$i];
  $date = date("Y-m-d", substr($orderDate, 0, 10));
  $fromHour= 15;
  $toHour= 18;
  $name = "Per";
  $description = "Lesing";
  $mail = "per@mail.com";
  $room_ID = 43; 
  
  $sql=mysqli_query($con,"insert into Orders(OrderDate, FromHour, ToHour, Name, Description, Mail, Room_ID) values('$date','$fromHour','$toHour','$name','$description','$mail','$room_ID');");
 
  if($sql){
    echo "post order ok";
  }else{
    die(mysql_error());
    }
 }




 mysqli_close($con);
?>