package analyzer;

public interface SearchStrategy {
    static SearchStrategy KMP() {
        return KMP::search;
    }

    static SearchStrategy RabinKarp() {
        return RabinKarp::search;
    }

    boolean search(String text, String pattern);
}
