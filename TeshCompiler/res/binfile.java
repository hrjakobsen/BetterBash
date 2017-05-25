public class binfile {
    public String name;
    public int error;
    public String directory;

    public binfile() {
        name = new String();
        error = 0;
        directory = new String();
    }

    public binfile clone() {
        binfile b = new binfile();
        b.name = this.name;
        b.error = this.error;
        b.directory = this.directory;
        return b;
    }
}