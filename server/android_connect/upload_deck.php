<?php
        require "init.php";

        $json_string = $_POST["json_string"];
	
        $json_obj = json_decode($json_string);
	$deck = $json_obj->deck;
        $pid = $deck->pid;
        $id = $deck->id;
        $date = $deck->date;
        $name = $deck->name;
        $num_cards = $deck->num_cards;
        $creator = $deck->creator;
        $num_downloads = 0;

        $check_dup_query = "SELECT id FROM decks WHERE id LIKE '$id';";
        $result = mysqli_query($con, $check_dup_query);
        
	$sql_query = "INSERT INTO decks (pid, id, date, name, num_cards, creator, num_downloads, price) VALUES ('$pid', '$id', '$date', '$name', '$num_cards', '$creator', 0, 0);";

	if (mysqli_num_rows($result) > 0) {
                echo "Upload faild! Deck already exists!";
        }
	else if (!mysqli_query($con, $sql_query)) {
		echo "Upload failed!" . mysqli_error($con);
	}
        else {
		$cards = $json_obj->cards;
                $cardsSize = count($cards);
		
		$sql_query = "INSERT INTO `cards`(`pid`, `id`, `date`, `name`, `detail`, `color`, `image`, `last_visit_date`, `num_forget`, `num_forget_over_dates`, `creator`) VALUES ";

		for ($i = 0; $i < $cardsSize; $i++) {
			$card_pid = $cards[$i]->pid;
			$card_id = $cards[$i]->id;
			$card_date = $cards[$i]->date;
			$card_name = $cards[$i]->name;
			$card_detail = $cards[$i]->detail;
			$card_color = $cards[$i]->color;
			$card_image = $cards[$i]->image;
			$card_last_visit_date = $cards[$i]->last_visit_date;
			$card_num_forget = $cards[$i]->num_forget;
			$card_num_forget_over_dates = $cards[$i]->num_forget_over_dates;
			$card_creator = $cards[$i]->creator;			

            		$sql_query .= "('$card_pid', '$card_id', '$card_date', '$card_name', '$card_detail', '$card_color', '$card_image', '$card_last_visit_date', '$card_num_forget', '$card_num_forget_over_dates', '$card_creator')";

			if ($i < ($cardsSize - 1)) {
				$sql_query .= ', ';
			}

		}
                if (mysqli_query($con, $sql_query)) {
                        echo "Upload success!";
                }
                else {
                        echo "Upload failed! ". mysqli_error($con);
                }
        }

        mysqli_close($con);

?>

