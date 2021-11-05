package exercises09.fourth;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Stream;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class ReadWords {

    public static void main(String[] args) {
//    9.4.2
        var display = new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull String s) {
                System.out.println(s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                System.out.println("an error occurred" + e.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("completed");
            }
        };
        //        9.4.2
        readWords().subscribe(display);
         //        9.4.3
        readWords().take(100).subscribe(display);
        //        9.4.4
        readWords().filter(x->x.length()>22).subscribe(display);

    }

    //    9.4.1
    public static Observable<String> readWords() {

        try {
            BufferedReader reader = new BufferedReader(new FileReader("english-words.txt"));
            var data = reader.lines();
            var words$ = Observable.fromIterable(data::iterator);
            return words$;


        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
