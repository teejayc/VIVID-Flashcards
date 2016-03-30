<?php

	require "init.php";

	$user_name = $_POST["user_name"];
	$user_id = $_POST["user_id"];
	$user_pass = $_POST["user_pass"];

	$check_dup_id_query = "SELECT user_id FROM user_info WHERE user_id LIKE '$user_id';";
	$result = mysqli_query($con, $check_dup_id_query);
	if (mysqli_num_rows($result) > 0) {
		echo "Registration failed! An user ID with $user_id already exists. Please try another ID.";
	}
	else {
		$sql_query = "INSERT INTO user_info VALUES('$user_name', '$user_id', '$user_pass', 0);";

		if (mysqli_query($con, $sql_query)) {
			echo "Registration success!";
		}
		else {
			echo "Registration failed!".mysqli_error($con);
		}
	}



?>