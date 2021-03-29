package Authentication;

import java.util.ArrayList;

public class NamesRepository implements Container {

    @Override
    public Iterator getIterator(ArrayList<String> names) {
        return new NameIterator(names);
    }

    private static class NameIterator implements Iterator {
        int index;
        ArrayList<String> names;
        public NameIterator(ArrayList<String> names) {
            this.names = names;
        }


        @Override
        public boolean hasNext() {
            return index < names.size();
        }

        @Override
        public Object next() {
            if(this.hasNext()){
                return names.get(index++);
            }
            return null;
        }
    }
}