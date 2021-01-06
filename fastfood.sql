SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

-- --------------------------------------------------------

CREATE TABLE `ff_games` (
  `start_time` int(32) NOT NULL DEFAULT '0',
  `end_time` int(32) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

CREATE TABLE `ff_game_scores` (
  `user_id` int(11) NOT NULL DEFAULT '0',
  `game_id` int(11) NOT NULL DEFAULT '0',
  `score, missiles` int(11) NOT NULL DEFAULT '0',
  `shields` int(11) NOT NULL DEFAULT '0',
  `parachutes` int(11) NOT NULL DEFAULT '0',
  `empty_plates` int(11) NOT NULL DEFAULT '0',
  `crashed_plates` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

CREATE TABLE `ff_login_tickets` (
  `user_id` int(11) NOT NULL DEFAULT '0',
  `ticket` varchar(128) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

CREATE TABLE `ff_statistics` (
  `missiles_launched` int(11) NOT NULL DEFAULT '0',
  `shields_used` int(11) NOT NULL DEFAULT '0',
  `big_parachutes_used` int(11) NOT NULL DEFAULT '0',
  `shield_defence` int(11) NOT NULL DEFAULT '0',
  `plates_landed` int(11) NOT NULL DEFAULT '0',
  `plates_crashed` int(11) NOT NULL DEFAULT '0',
  `games_played` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

CREATE TABLE `ff_users` (
  `id` int(11) NOT NULL,
  `arcturus_account` varchar(32) NOT NULL DEFAULT '',
  `hotel_user_id` int(11) NOT NULL DEFAULT '0',
  `hotel_user_name` varchar(32) NOT NULL DEFAULT '',
  `hotel_user_look` varchar(64) NOT NULL DEFAULT '',
  `hotel_name` varchar(32) NOT NULL DEFAULT '',
  `bigparachutes` int(11) NOT NULL DEFAULT '3',
  `missiles` int(11) NOT NULL DEFAULT '3',
  `shields` int(11) NOT NULL DEFAULT '3',
  `last_seen` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `ff_login_tickets`
  ADD UNIQUE KEY `user_id` (`user_id`);

ALTER TABLE `ff_users`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `ff_users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
