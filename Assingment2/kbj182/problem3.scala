/*
	Pre conditions: n is not larger than the length of seq
	Post Conditions: The length of the returned sequence is the same length as n
*/

def sampleWithoutReplacement[T](seq:Seq[T],n:Int) = {
	require(seq.length >= n);
	val strm = Stream.continually(nextInt(seq.length)).distinct.take(n).force;
	strm.map(x => seq(x)).force
}.ensuring(ret => ret.length == n)