package hr.java.clientcommunication.entity;
//tipa da mi primi jednog employeea i sve njegove pozive ili npr broj poziva, ili jednog klijenta i sve njegove pozive ili jednog zaposlenika i sve njegove klijente
//imat ce mi samo neki tostring za ispisivanje
public class ReportingTool<K, V> extends Entity{
    private K key;
    private V value;

    public ReportingTool(Long id, K key, V value) {
        super(id);
        this.key = key;
        this.value = value;
    }

    public ReportingTool(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Pair{" + "key=" + key + ", value=" + value + '}';
    }
}
