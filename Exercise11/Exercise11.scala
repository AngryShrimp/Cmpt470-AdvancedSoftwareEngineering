/*
 *	Keenan Johnstone
 *	11119412
 *	kbj182
 *	cmpt470 Exercise 11
 */

import scala.io.Source

def parseFile(str: String) {
	for (line <- Source.fromFile(str).getLines()){
		println(parseYearMonthDay(line))
	}
}

def parseYearMonthDay(str: String) = {
	val Format1 = """^([0-9]{1,2})-([0-9]{1,2})-([0-9]{4})\t([0-9]+)""".r;
	val Format2 = """^([0-9]{1,2})/([0-9]{1,2})/([0-9]{4})\t([0-9]+)""".r;
	val Format3 = """^([a-zA-Z]+), ([a-zA-Z]+) ([0-9]{2}), ([0-9]{4})\t([0-9]+)""".r;
	val Format4 = """^([0-9]{1,2})-([a-zA-Z]+)-([0-9]{4})\t([0-9]+)""".r;
	str match { 
		case Format1(month, day, year, num) => s"$year\t$month\t$day\t$num"
		case Format2(month, day, year, num) => s"$year\t$month\t$day\t$num"
		case Format3(dayOfWeek, monthText, day, year, num) => s"$year\t" + monFullTextToInt(monthText) + s"\t$day" + s"\t$num"
		case Format4(day, monthText, year, num) => s"$year\t" + mon3TextToInt(monthText) + s"\t$day" + s"\t$num"
		case _           => println("No match")
 	}
}

def mon3TextToInt(str: String) = {
	str match {
		case "Jan" => 1
		case "Feb" => 2
		case "Mar" => 3
		case "Apr" => 4
		case "May" => 5
		case "Jun" => 6
		case "Jul" => 7
		case "Aug" => 8
		case "Sep" => 9
		case "Oct" => 10
		case "Nov" => 11
		case "Dec" => 12
		case _     => -1
	}
}

def monFullTextToInt(str: String) = {
	str match {
		case "January" => 1
		case "February" => 2
		case "March" => 3
		case "April" => 4
		case "May" => 5
		case "June" => 6
		case "July" => 7
		case "August" => 8
		case "September" => 9
		case "October" => 10
		case "November" => 11
		case "December" => 12
		case _ => -1
	}
}