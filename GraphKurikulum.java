public class GraphKurikulum {
     LinkedList semua;

    public GraphKurikulum(LinkedList semua) {
        this.semua = semua;
    }

    boolean sudahAdaDiDaftar(LinkedList daftar, MataKuliah mk) {
        Node temp = daftar.head;
        while (temp != null) {
            if (temp.data == mk) {
                return true;
            }
            temp = temp.next;
        }
        return false;
    }

    public LinkedList prasyaratBelumLulus(MataKuliah mk) {
        LinkedList hasil = new LinkedList();
        cariPrasyarat(mk, hasil);
        return hasil;
    }

    void cariPrasyarat(MataKuliah mk, LinkedList hasil) {
        Node temp = mk.daftarPrasyarat.head;
        while (temp != null) {
            MataKuliah pra = temp.data;
            if (pra.lulus == false && sudahAdaDiDaftar(hasil, pra) == false) {
                cariPrasyarat(pra, hasil);
                if (sudahAdaDiDaftar(hasil, pra) == false) {
                    hasil.add(pra);
                }
            }
            temp = temp.next;
        }
    }

    public LinkedList sisaMKWajib() {
        LinkedList sisa = new LinkedList();
        Node temp = semua.head;
        while (temp != null) {
            MataKuliah mk = temp.data;
            if (mk.kodeMK.equalsIgnoreCase("SKR") == false && mk.lulus == false) {
                sisa.add(mk);
            }
            temp = temp.next;
        }
        return sisa;
    }
}
