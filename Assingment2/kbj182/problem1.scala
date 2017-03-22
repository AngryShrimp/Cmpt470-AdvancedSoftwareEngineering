
/*
	Pre conditions: Vec1 is of the same length as vec2
	Post Conditions: The returned value is not negative
*/
def vecSumSquareDiff(vec1:Vector[Double], vec2:Vector[Double]) = {
	require(vec1.length == vec2.length);
	vec1.zip(vec2).map(v => pow(abs(v._1 - v._2), 2)).sum
}.ensuring(ret => ret >= 0)

/*
	Pre conditions: seq has 2 or more elements, the index is not greater than the length of the seq
	Post Conditions: The "furthest" value is the furthest value from the index
*/
def indexForFurthestElt(seq:Seq[Double], i:Int) = {
    require(seq.length >= 2);
    require(i >= 0 && i < seq.length);
    val refElt = seq(i);
    seq.zipWithIndex.map(pair => (abs(pair._1-refElt), pair._2)).sortWith((pair1, pair2) => pair1._1 > pair2._1)(0)._2
}.ensuring(iFurthest =>  abs(seq(iFurthest)-seq(i)) >= abs(seq(0)-seq(i)))