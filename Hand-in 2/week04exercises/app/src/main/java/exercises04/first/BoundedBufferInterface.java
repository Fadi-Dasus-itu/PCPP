package exercises04.first;

interface BoundedBufferInterface<T> {
     T take() throws Exception;
     void insert(T elem) throws Exception;
}
