public class MataKuliah {
    String kodeMK,NamaMK,Semester;
    int sks;
    boolean lulus=false;
    LinkedList daftarPrasyarat;
    public MataKuliah(String kode, String nama, int sks, String ganjilGenap) {
        this.kodeMK = kode;
        this.NamaMK = nama;
        this.sks = sks;
        this.Semester = ganjilGenap;
        daftarPrasyarat= new LinkedList();
    }
}
