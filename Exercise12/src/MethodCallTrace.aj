import org.aspectj.lang.Signature;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Keenan on 2017-04-03.
 */
public aspect MethodCallTrace {


    HashMap<String, Integer> hashMap = new HashMap<>();
    pointcut finalPrint() : (execution(* ConwaysGameOfLife.writeState(..)));
    pointcut traceMethods() : (execution(* *(..))&& !cflow(within(Trace)));


    before(): traceMethods(){
        Signature sig = thisJoinPointStaticPart.getSignature();
        String line =""+ thisJoinPointStaticPart.getSourceLocation().getLine();
        String sourceName = thisJoinPointStaticPart.getSourceLocation().getWithinType().getCanonicalName();

        String callStringKey = "Call from " +  sourceName +" line " + line +" to " +sig.getDeclaringTypeName() + "." + sig.getName();

        //If the key exists increment the value of the key
        if(hashMap.containsKey(callStringKey)){
            hashMap.put(callStringKey, hashMap.get(callStringKey) + 1);
        }
        //Else add the key!
        else{
            hashMap.put(callStringKey, 1);
        }
    }

    after() returning(): finalPrint(){
        for(String functionCall: hashMap.keySet()){
            String key = functionCall.toString();
            String value = hashMap.get(functionCall).toString();
            System.out.println(key + " was called: " + value + " times");
        }
    }


}
