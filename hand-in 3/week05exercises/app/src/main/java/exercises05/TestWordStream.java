// Week 3
// sestoft@itu.dk * 2015-09-09
package exercises05;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TestWordStream {

    public static void main(String[] args) {
//        1:
        System.out.println(readWords().count());
//        -----------------------------------------------------------------------------------------------------------------

//        2: Write a stream pipeline to print the first 100 words from the file.
        readWords().limit(100).forEach(System.out::println);
//        -----------------------------------------------------------------------------------------------------------------

//        3: Write a stream pipeline to find and print all words that have at least 22 letters.
        readWords().filter(x-> x.length()>=22).forEach(System.out::println);
//        -----------------------------------------------------------------------------------------------------------------

//        4: Write a stream pipeline to find and print some word that has at least 22 letters
        readWords().filter(x-> x.length()>=22).findAny().ifPresent(System.out::println);
//        -----------------------------------------------------------------------------------------------------------------

//        5: Write a method boolean isPalindrome - Write a stream pipeline to find all palindromes and print them
        readWords().filter(TestWordStream::isPalindrome).forEach(System.out::println);

//        -----------------------------------------------------------------------------------------------------------------

//        6. Make a parallel version of the palindrome-printing stream pipeline. Is it possible to observe whether it is
//        faster or slower than the sequential one?
//        the parallel execution time is41 milliseconds
//        the sequential execution time is66 milliseconds

        long startSequential = System.nanoTime();
        readWords().filter(TestWordStream::isPalindrome).forEach(System.out::println);
        long durationSequential = (System.nanoTime() - startSequential) / 1000000;
        System.out.println("//////////////////////////////////////////////////////////////////////////sequential");
        System.out.println(" the sequential execution time is" + durationSequential + " milliseconds");
        System.out.println("//////////////////////////////////////////////////////////////////////////sequential");

        long startParallel = System.nanoTime();
        readWords().filter(TestWordStream::isPalindrome).parallel().unordered().forEach(System.out::println);
        long durationParallel = (System.nanoTime() - startParallel) / 1000000;
        System.out.println("//////////////////////////////////////////////////////////////////////////parallel");
        System.out.println("the parallel execution time is" + durationParallel + " milliseconds");
        System.out.println("//////////////////////////////////////////////////////////////////////////parallel");


//        -----------------------------------------------------------------------------------------------------------------
//        7. Use a stream pipeline that turns the stream of words into a stream of their lengths, to find and print the minimal, maximal and average word lengths.

        System.out.println(readWords().mapToInt(String::length).min().getAsInt());
        System.out.println(readWords().mapToInt(String::length).max().getAsInt());
        System.out.println(readWords().mapToInt(String::length).average().getAsDouble());
//        -----------------------------------------------------------------------------------------------------------------

//        8. Write a stream pipeline, using method collect and a groupingBy collector from class Collectors, to group the words by length

        Map<Integer, List<String>> wordGrouping = readWords().collect(Collectors.groupingBy(String::length));
        for (Map.Entry<Integer, List<String>> entry : wordGrouping.entrySet())
            System.out.println("Key = " + entry.getKey() +
                    ", Value = " + entry.getValue());
//        -----------------------------------------------------------------------------------------------------------------

//        9. Write a method Map<Character,Integer> letters
//        Now write a stream pipeline that transforms all the English words into the corresponding tree map of letter
//        counts, and print this for the first 100 words.

        readWords().limit(100).map(TestWordStream::letters).forEach(System.out::println);
//        -----------------------------------------------------------------------------------------------------------------

//        10. Use the tree map stream to write a stream pipeline to count the total number of times the letter e is used in
//        the English words. For the words file on the course homepage the result should be 235,331.

        var letterECounter = readWords()
                .map(TestWordStream::letters)
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        Integer::sum
                ))
                .get('e');
        System.out.println(letterECounter);
    }

    public static Stream<String> readWords() {

        try {
            BufferedReader reader = new BufferedReader(new FileReader("english-words.txt"));
            return reader.lines().parallel().unordered();

        } catch (IOException e) {
            System.out.println(e.getMessage());
            return Stream.empty();
        }
    }

    public static boolean isPalindrome(String word) {
        StringBuilder sb = new StringBuilder(word);
        sb.reverse();
        String rev = sb.toString();
        return word.equals(rev);
    }

    public static Map<Character, Integer> letters(String word) {
        Map<Character, Integer> charactersMap = new TreeMap<>();
        char[] characterArray = word.toLowerCase(Locale.ROOT).toCharArray();
        Stream<Character> cStream = IntStream.range(0, characterArray.length).mapToObj(i -> characterArray[i]);
        cStream.forEach(c->  charactersMap.put(c, charactersMap.containsKey(c) ? charactersMap.get(c) + 1 : 1));
        return charactersMap;
    }
}
