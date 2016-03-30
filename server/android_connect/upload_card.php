<?php

	require "init.php";

	$pid = $_POST["pid"];
	$id = $_POST["id"];
	$date = $_POST["date"];
	$name = $_POST["name"];
	$detail = $_POST["detail"];
	$color = $_POST["color"];
	$image = $_POST["image"];
	$last_visit_date = $_POST["last_visit_date"];
	$num_forget = $_POST["num_forget"];
	$num_forget_over_dates = $_POST["num_forget_over_dates"];
	$creator = $_POST["creator"];
	

	$sql_query = "INSERT INTO cards (pid, id, date, name, detail, color, image, last_visit_date,
									num_forget, num_forget_over_dates, creator) 
									VALUES($pid, $id, $date, $name, $detail, $color, $image,
									$last_visit_date, $num_forget, $num_forget_over_dates, $creator);";

	if (mysqli_query($con, $sql_query)) {
		echo "Upload success!";
	}
	else {
		echo "Upload failed!".mysqli_error($con);
	}

	mysqli_close($con);

?>