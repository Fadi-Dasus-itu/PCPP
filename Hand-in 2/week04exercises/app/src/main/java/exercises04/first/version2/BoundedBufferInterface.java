package exercises04.first.version2;

public interface BoundedBufferInterface<T> {
     T take() throws Exception;
     void insert(T elem) throws Exception;
}
