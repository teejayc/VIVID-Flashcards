<?php

	require "init.php";

	$query = "SELECT * FROM decks";

	$result = mysqli_query($con, $query);

	$response = array();


	while ($row = mysqli_fetch_array($result)) {
		array_push($response, array(
			"pid"=>$row[1],
			"id"=>$row[2],
			"date"=>$row[3],
			"name"=>$row[4],
			"image"=>$row[5],
			"numCards"=>$row[6],
			"creator"=>$row[7]));
	}


	echo json_encode(array("server_response"=>$response));

	mysqli_close($con);
?>