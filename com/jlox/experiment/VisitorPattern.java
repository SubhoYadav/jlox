package com.jlox.experiment;
// This is an example of visitor pattern in java

public class VisitorPattern {
    public interface Animal {
        void accept(AnimalVisitor visitor);
    }

    public interface AnimalVisitor {
        void visitMonkey(Monkey monkey);
        void visitLion(Lion monkey);
        void visitDolphin(Dolphin monkey);
    }

    class Monkey implements Animal {
        @Override
        public void accept(AnimalVisitor visitor) {
            visitor.visitMonkey(this);
        }
    }

    class Lion implements Animal {
        @Override
        public void accept(AnimalVisitor visitor) {
            visitor.visitLion(this);
        }
    }

    class Dolphin implements Animal {
        @Override
        public void accept(AnimalVisitor visitor) {
            visitor.visitDolphin(this);
        }
    }

    class Speak implements AnimalVisitor {
        @Override
        public void visitDolphin(VisitorPattern.Dolphin monkey) {
            System.out.println("Dolphin is speaking...");
        }

        @Override
        public void visitLion(VisitorPattern.Lion monkey) {
            System.out.println("Lion is spaking...");
        }

        @Override
        public void visitMonkey(VisitorPattern.Monkey monkey) {
            System.out.println("Monkey is speaking...");
        }
    }

    class Jump implements AnimalVisitor {
        @Override
        public void visitDolphin(VisitorPattern.Dolphin monkey) {
            System.out.println("Dolphin is jumping...");
        }

        @Override
        public void visitLion(VisitorPattern.Lion monkey) {
            System.out.println("Lion is jumping...");
        }

        @Override
        public void visitMonkey(VisitorPattern.Monkey monkey) {
            System.out.println("Monkey is jumping...");
        }
    }

    public static void main(String[] args) {
        VisitorPattern vp = new VisitorPattern();

        Lion lion = vp.new Lion();
        Monkey monkey = vp.new Monkey();
        Dolphin dolphin = vp.new Dolphin();

        Speak speak = vp.new Speak();

        // Now our animals will speak
        lion.accept(speak);
        monkey.accept(speak);
        dolphin.accept(speak);

        Jump jump = vp.new Jump();

        // Now our animals will jump
        lion.accept(jump);
        monkey.accept(jump);
        dolphin.accept(jump);
    }
}
