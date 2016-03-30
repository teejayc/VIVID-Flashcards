<?php

	require "init.php";

	$deck_id = $_POST["deck_id"];

	$query = "SELECT * FROM cards WHERE pid LIKE deck_id";

	$result = mysqli_query($con, $query);

	$response = array();


	while ($row = mysqli_fetch_array($result)) {
		array_push($response, array(
			"pid"=>$row[1],
			"id"=>$row[2],
			"date"=>$row[3],
			"name"=>$row[4],
			"detail"=>$row[5],
			"color"=>$row[6],
			"image"=>$row[7],
			"last_visit_date"=>$row[8],
			"num_forget"=>$row[9],
			"num_forget_over_dates"=>$row[10],
			"creator"=>$row[11]));
	}


	echo json_encode(array("server_response"=>$response));

	mysqli_close($con);
?>