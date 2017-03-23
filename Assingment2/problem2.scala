/*
*/

def optionParser(seq:Seq[Option[Any]]):Option[Any] = Option( if(seq contains Option(None)){None} else {seq.flatten})