import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Pemakaian: java Main <matakuliah.csv> <prasyarat.csv> <mhs_xx.txt>");
            System.exit(1);
        }
        try {
            LinkedList semua = bacaMataKuliah(args[0]);
            bacaPrasyarat(args[1], semua);
            String semester = bacaMahasiswa(args[2], semua);
            tampilkanRekomendasi(semua, semester);
        } catch (IOException e) {
            System.err.println("Gagal membaca berkas: " + e.getMessage());
            System.exit(1);
        }
    }

        private static String bersih(String s) {
        return s.replace("\uFEFF", "").trim();
    }

        private static LinkedList bacaMataKuliah(String path) throws IOException {
        LinkedList semua = new LinkedList();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine();
            String baris;
            while ((baris = br.readLine()) != null) {
                baris = bersih(baris);
                if (baris.isEmpty()) continue;
                String[] f = baris.split(";");
                if (f.length < 5) {
                    System.err.println("Baris MK diabaikan: " + baris);
                    continue;
                }
                semua.add(new MataKuliah(bersih(f[0]), bersih(f[1]),
                        Integer.parseInt(bersih(f[2])), bersih(f[4])));
            }
        }
        return semua;
    }

        private static void bacaPrasyarat(String path, LinkedList semua) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine();
            String baris;
            while ((baris = br.readLine()) != null) {
                baris = bersih(baris);
                if (baris.isEmpty()) continue;
                String[] f = baris.split(";");
                MataKuliah pra = f.length >= 2 ? semua.findByKode(bersih(f[0])) : null;
                MataKuliah mk  = f.length >= 2 ? semua.findByKode(bersih(f[1])) : null;
                if (pra == null || mk == null) {
                    System.err.println("Baris prasyarat diabaikan: " + baris);
                    continue;
                }
                mk.daftarPrasyarat.add(pra);
            }
        }
    }

        private static String bacaMahasiswa(String path, LinkedList semua) throws IOException {
        String semester = null;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String baris;
            while ((baris = br.readLine()) != null) {
                baris = bersih(baris);
                int eq = baris.indexOf('=');
                if (eq < 0) continue;
                String kunci = baris.substring(0, eq).trim().toLowerCase();
                String nilai = baris.substring(eq + 1).trim();

                if (kunci.equals("semester_berikutnya")) {
                    semester = nilai.toLowerCase();
                } else if (kunci.equals("lulus")) {
                    for (String kode : nilai.split(",")) {
                        kode = bersih(kode);
                        if (kode.isEmpty()) continue;
                        MataKuliah mk = semua.findByKode(kode);
                        if (mk == null) System.err.println("Peringatan: kode MK tidak dikenal: " + kode);
                        else mk.lulus = true;
                    }
                }
            }
        }
        if (semester == null || !(semester.equals("ganjil") || semester.equals("genap"))) {
            System.err.println("semester_berikutnya harus 'ganjil' atau 'genap'.");
            System.exit(1);
        }
        return semester;
    }

        private static boolean ditawarkan(MataKuliah mk, String semester) {
        return mk.Semester.equalsIgnoreCase("Keduanya") || mk.Semester.equalsIgnoreCase(semester);
    }

    private static String gabungKode(LinkedList daftar) {
        StringBuilder sb = new StringBuilder();
        for (Node n = daftar.head; n != null; n = n.next) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(n.data.kodeMK);
        }
        return sb.toString();
    }

    private static void tampilkanRekomendasi(LinkedList semua, String semester) {
        GraphKurikulum graph = new GraphKurikulum(semua);

        LinkedList bisa = new LinkedList();
        StringBuilder belum = new StringBuilder();

        for (Node n = semua.head; n != null; n = n.next) {
            MataKuliah mk = n.data;
            if (mk.lulus) continue;
            if (!ditawarkan(mk, semester)) continue;

            boolean skripsi = mk.kodeMK.equalsIgnoreCase("SKR");
            LinkedList kurang = skripsi ? graph.sisaMKWajib()
                                        : graph.prasyaratBelumLulus(mk);

            if (kurang.size == 0) {
                bisa.add(mk);
            } else if (skripsi) {
                belum.append(mk.kodeMK).append(": belum lulus semua MK wajib (")
                     .append(gabungKode(kurang)).append(")\n");
            } else {
                belum.append(mk.kodeMK).append(": belum lulus ").append(gabungKode(kurang)).append("\n");
            }
        }

        System.out.println("Daftar MK yang bisa diambil:");
        int no = 1;
        for (Node n = bisa.head; n != null; n = n.next, no++)
            System.out.println(no + ". " + n.data.kodeMK + " " + n.data.NamaMK + " " + n.data.sks + " SKS");
        if (bisa.size == 0) System.out.println("(tidak ada)");

        System.out.println();
        System.out.println("Daftar MK yang belum bisa diambil semester depan:");
        System.out.print(belum.length() == 0 ? "(tidak ada)\n" : belum.toString());

        System.out.println();
        System.out.println("Persyaratan khusus : Skripsi hanya bisa diambil setelah semua MK wajib diambil");
    }

    }