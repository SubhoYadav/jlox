public class Tester {
    interface Animal {
        void accept(AnimalVisitor visisitor);
    }

    interface AnimalVisitor {
        void visitMonkey(Animal monkey);
        void visitLion(Animal lion);
        void visitDolphin(Animal dolphin);
    }

    class Speak implements AnimalVisitor {
        @Override
        public void visitDolphin(Tester.Animal dolphin) {
            System.out.println("Dolphin speaking...");
        }

        @Override
        public void visitLion(Tester.Animal lion) {
            System.out.println("Lion speaking...");
        }

        @Override
        public void visitMonkey(Tester.Animal monkey) {
            System.out.println("Monkey speaking...");
        }
    }

    class Jump implements AnimalVisitor {
        @Override
        public void visitDolphin(Tester.Animal dolphin) {
            System.out.println("Dolphin jumping...");
        }

        @Override
        public void visitLion(Tester.Animal lion) {
            System.out.println("Lion jumping...");
        }

        @Override
        public void visitMonkey(Tester.Animal monkey) {
            System.out.println("Monkey jumping...");
        }
    }

    class Monkey implements Animal {
        @Override
        public void accept(Tester.AnimalVisitor visisitor) {
            visisitor.visitMonkey(this);
        }
    }

    class Lion implements Animal {
        @Override
        public void accept(Tester.AnimalVisitor visisitor) {
            visisitor.visitLion(this);
        }
    }

    class Dolphin implements Animal {
        @Override
        public void accept(Tester.AnimalVisitor visisitor) {
            visisitor.visitDolphin(this);
        }
    }



    public static void main(String[] args) {
        Tester tester = new Tester();

        Lion lion = tester.new Lion();
        Monkey monkey = tester.new Monkey();
        Dolphin dolphin = tester.new Dolphin();

        Speak speak = tester.new Speak();

        // Make animals speak
        lion.accept(speak);
        dolphin.accept(speak);
        monkey.accept(speak);

        Jump jump = tester.new Jump();

        // Make animals jump
        lion.accept(jump);
        dolphin.accept(jump);
        monkey.accept(jump);
    }
}