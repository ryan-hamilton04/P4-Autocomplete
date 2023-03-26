import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HashListAutocomplete implements Autocompletor {
    private static final int MAX_PREFIX = 10;
    private Map<String, List<Term>> myMap;
    private int mySize;

    public HashListAutocomplete(String[] terms, double[] weights) {
        if (terms == null || weights == null) {
                throw new NullPointerException("One or both of terms and weights are null");
            }
            if (terms.length != weights.length) {
                throw new IllegalArgumentException("Terms and weights are not the same length");
            }
            initialize(terms, weights);
    }

    @Override
    public List<Term> topMatches(String prefix, int k) {
        if (prefix.length() > MAX_PREFIX) {
        prefix = prefix.substring(0, MAX_PREFIX);
        }
        List<Term> all = myMap.getOrDefault(prefix, Collections.emptyList());
        List<Term> list = all.subList(0, Math.min(k, all.size()));
        return list;
    }

    @Override
    public void initialize(String[] terms, double[] weights) {
        myMap = new HashMap<>();
        mySize = 0;
        for (int i = 0; i < terms.length; i++) {
            String term = terms[i];
            double weight = weights[i];
            Term t = new Term(term, weight);
            for (int j = 0; j <= MAX_PREFIX && j <= term.length(); j++) {
                String prefix = term.substring(0, j);
                if (!myMap.containsKey(prefix)) {
                    myMap.put(prefix, new ArrayList<>());
                }
                myMap.get(prefix).add(t);
            }
        }
        for (String prefix : myMap.keySet()) {
            Collections.sort(myMap.get(prefix), Collections.reverseOrder(Comparator.comparing(Term::getWeight)));
        }
    }

    @Override
    public int sizeInBytes() {
    if (mySize == 0) {
        for (String key : myMap.keySet()) {
            mySize += BYTES_PER_CHAR * key.length();
            List<Term> value = myMap.get(key);
            for (Term term : value) {
                mySize += BYTES_PER_CHAR * term.getWord().length();
                mySize += BYTES_PER_DOUBLE;
            }
        }
    }
    System.out.println(mySize);
    return mySize;
    }
    
}

