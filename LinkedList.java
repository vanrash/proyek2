class Node{
    MataKuliah data;
    Node next;
    public Node(MataKuliah data){
        this.data=data;
        next=null;
    }
}
public class LinkedList {
    Node head;
    Node tail;
    int size=0;

    public void add(MataKuliah data){
        Node baru= new Node(data);
        if(head==null){
            head= baru;
            tail= baru;
            size++;
        }
        else if(size==1){
            head.next= baru;
            tail= baru;
            size++;
        }
        else{
            Node temp= head;
            while(temp.next!=null){
                temp=temp.next;
            }
            temp.next= baru;
            tail= baru;
            size++;
        }
    }

    public MataKuliah findByKode(String kode){
        Node temp=head;
        
        while(temp!=null){
            if(temp.data.kodeMK.equalsIgnoreCase(kode)){
                return temp.data;
            }
            temp=temp.next;
        }
        return null;
    }
}

