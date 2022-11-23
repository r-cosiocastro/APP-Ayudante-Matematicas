package com.rafaelcosio.mathhelper.models;

import android.content.Context;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import com.rafaelcosio.mathhelper.R;
import com.rafaelcosio.mathhelper.utils.Utils;

import org.parceler.Parcel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.rafaelcosio.mathhelper.utils.Constants.DIFICIL;
import static com.rafaelcosio.mathhelper.utils.Constants.DIV;
import static com.rafaelcosio.mathhelper.utils.Constants.FACIL;
import static com.rafaelcosio.mathhelper.utils.Constants.MEDIO;
import static com.rafaelcosio.mathhelper.utils.Constants.MULT;
import static com.rafaelcosio.mathhelper.utils.Constants.SUB;

@Parcel
public class Problem {
    private static final int SUM = 0;
    private static final int SUBTRACT = 1;
    private static final int MULTIPLICATION = 2;
    private static final int DIVISION = 3;
    private Long _id;
    private String description;
    private int result;
    private @Category
    int category;
    private boolean solved;
    private boolean correct;
    private boolean multipleAnswer;
    private List<Answer> possibleAnswers;
    private int givenAnswer;
    private boolean inverse;
    private int imageID;

    public Problem() {

    }

    private Problem(String description, int result, int category, boolean inverse, boolean multipleAnswer, List<Answer> possibleAnswers, int imageID) {
        this.description = description;
        this.result = result;
        this.category = category;
        this.solved = false;
        this.correct = false;
        this.inverse = inverse;
        this.multipleAnswer = multipleAnswer;
        this.possibleAnswers = possibleAnswers;
        this.imageID = imageID;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

    public List<Answer> getPossibleAnswers() {
        return possibleAnswers;
    }

    public String getDescription() {
        return description;
    }

    public int getResult() {
        return result;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public int getGivenAnswer() {
        return givenAnswer;
    }

    public void setGivenAnswer(int givenAnswer) {
        this.givenAnswer = givenAnswer;
    }

    public boolean isMultipleAnswer() {
        return multipleAnswer;
    }

    public void answer(int givenAnswer) {
        this.solved = true;
        this.correct = (this.result == givenAnswer);
        if (this.inverse) {
            this.correct = !this.correct;
        }
        this.givenAnswer = givenAnswer;
    }

    @Override
    public String toString() {
        return "Problem{" +
                "_id=" + _id +
                ", description='" + description + '\'' +
                ", result=" + result +
                ", category=" + category +
                ", solved=" + solved +
                ", correct=" + correct +
                ", multipleAnswer=" + multipleAnswer +
                ", possibleAnswers=" + possibleAnswers +
                ", givenAnswer=" + givenAnswer +
                '}';
    }

    public Problem generateProblem(Context context, int operation, int difficultyLevel, int seed) {
        int number1 = Utils.randomNumberMinMax(9, 12) * (difficultyLevel + 1);
        int number2 = Utils.randomNumberMinMax(5, 8) * (difficultyLevel + 1);
        int number3 = Utils.randomNumberMinMax(2, 10) * (difficultyLevel + 1);
        int aux1 = number1;
        int aux2 = number2;
        boolean inverse = false;
        boolean multipleAnswers = false;
        List<Answer> possibleAnswers = new ArrayList<>();
        int imageID = R.drawable.thinking;

        int[] objectDrawables = {R.drawable.vehicle,
                R.drawable.coin,
                R.drawable.adorno,
                R.drawable.vehicle,
                R.drawable.cuaderno,
                R.drawable.shirt};

        int[] foodDrawables = {R.drawable.apple,
                R.drawable.watermelon,
                R.drawable.chocolate,
                R.drawable.candies,
                R.drawable.pineapple,
                R.drawable.pear,
                R.drawable.cookie,
                R.drawable.bolillo,
                R.drawable.taco
        };

        String description = "";
        String objects[] = context.getResources().getStringArray(R.array.objects);
        String food[] = context.getResources().getStringArray(R.array.food);
        String peopleNames[] = context.getResources().getStringArray(R.array.people_names);
        String colours[] = context.getResources().getStringArray(R.array.colours);
        String flavours[] = context.getResources().getStringArray(R.array.flavour);
        List<String> flavourList = Arrays.asList(flavours);
        Collections.shuffle(flavourList);
        int foodIndex = Utils.randomNumber(food.length);
        int objectsIndex = Utils.randomNumber(objects.length);
        int colorIndex = Utils.randomNumber(colours.length);
        int nameIndex = Utils.randomNumber(peopleNames.length);
        int altNameIndex = Utils.randomNumber(peopleNames.length);
        boolean usesThirdNumber = false;
        boolean showHint = true;

        String operator = "";
        if (operation == SUM) {
            operator = " + ";
        } else if (operation == SUB) {
            operator = " - ";
        } else if (operation == MULT) {
            operator = " × ";
        } else if (operation == DIV) {
            operator = " ÷ ";
        }

        if (altNameIndex == nameIndex) {
            if (altNameIndex > 0) {
                altNameIndex--;
            } else {
                altNameIndex++;
            }
        }

        int result = 0;
        switch (operation) {
            //region Sum
            case SUM:
                switch (difficultyLevel) {
                    //region Level 1
                    case FACIL:
                        switch (seed) {
                            default:
                                description = "Tengo " + number1 + " " + objects[objectsIndex] + " y compro " + number2 + ", ¿cuánto tengo en total?";
                                imageID = objectDrawables[objectsIndex];
                                multipleAnswers = true;
                                possibleAnswers = generateAnswers("Ahora tengo %d %s", objects[objectsIndex], operation, difficultyLevel, number1, number2);
                                break;
                        }
                        break;
                    //endregion
                    //region Level 2
                    case MEDIO:
                        switch (seed) {
                            case 0:
                                description = context.getResources().getString(R.string.problem1,
                                        peopleNames[nameIndex],
                                        number1,
                                        food[foodIndex],
                                        number2);
                                multipleAnswers = true;
                                possibleAnswers.add(new Answer(context.getResources().getString(R.string.answer1, (number1 + number2), food[foodIndex], number1, number2), number1 + number2));
                                possibleAnswers.add(new Answer(context.getResources().getString(R.string.answer1, (number1 - 1 + number2), food[foodIndex], number1 - 1, number2), number1 - 1 + number2));
                                possibleAnswers.add(new Answer(context.getResources().getString(R.string.answer1, (number1 + number2 - 2), food[foodIndex], number1, number2 - 2), number1 + number2 - 2));
                                imageID = foodDrawables[foodIndex];
                                break;

                            case 1:
                                description = context.getResources().getString(R.string.problem2,
                                        peopleNames[nameIndex],
                                        number1,
                                        objects[objectsIndex],
                                        colours[colorIndex],
                                        number2,
                                        colours[Utils.randomNumber(colours.length)]);
                                multipleAnswers = true;
                                possibleAnswers.add(new Answer(context.getResources().getString(R.string.answer2, (aux1 + aux2), objects[objectsIndex], aux1, aux2), aux1 + aux2));
                                aux1--;
                                possibleAnswers.add(new Answer(context.getResources().getString(R.string.answer2, (aux1 + aux2), objects[objectsIndex], aux1, aux2), aux1 + aux2));
                                aux2 -= 2;
                                possibleAnswers.add(new Answer(context.getResources().getString(R.string.answer2, (aux1 + aux2), objects[objectsIndex], aux1, aux2), aux1 + aux2));
                                imageID = objectDrawables[objectsIndex];
                                break;
                            case 2:
                                description = context.getResources().getString(R.string.problem3,
                                        number1, number1 * 2, number1 * 3);
                                result = number1 * 4;
                                showHint = false;
                                break;
                            case 3:
                                description = context.getResources().getString(R.string.problem4, number1, number2, number1 + number2);
                                multipleAnswers = true;
                                possibleAnswers.add(new Answer(context.getResources().getString(R.string.answer4, number2, number1, number1 + number2), number1 + number2));
                                aux1 -= 2;
                                possibleAnswers.add(new Answer(context.getResources().getString(R.string.answer4, aux1, aux2, aux1 + aux2), aux1 + aux2));
                                aux2 -= 2;
                                possibleAnswers.add(new Answer(context.getResources().getString(R.string.answer4, aux1, aux2, aux1 + aux2), aux1 + aux2));
                                break;
                            case 4:
                                description = context.getResources().getString(R.string.problem5, number1 + number2);
                                multipleAnswers = true;
                                possibleAnswers.add(new Answer(context.getResources().getString(R.string.answer5, number1 + 3, number2 - 2), number1 + 3 + number2 - 2));
                                possibleAnswers.add(new Answer(context.getResources().getString(R.string.answer5, number1, number2), number1 + number2));
                                possibleAnswers.add(new Answer(context.getResources().getString(R.string.answer5, number1 + number2, 0), number1 + number2));
                                inverse = true;
                                break;
                        }
                        break;
                    //endregion
                    //region Level 3
                    case DIFICIL:
                        switch (seed) {
                            case 0:
                                description = "En la fiesta hay " + number1 + " bocadillos de " + flavourList.get(0) +
                                        ", " + number2 + " de " + flavourList.get(1) +
                                        " y " + number3 + " de " + flavourList.get(2) +
                                        ". ¿Cuántos bocadillos hay?";
                                result = number1 + number2 + number3;
                                usesThirdNumber = true;
                                break;

                            case 1:
                                description = "Laura colecciona cromos de animales, tiene " + number1 + " de gatos, " + number2 + " de loros y " + number3 + " de perros. ¿Cuántos tiene en total?";
                                result = number1 + number2 + number3;
                                usesThirdNumber = true;
                                imageID = R.drawable.cromos;
                                break;
                            case 2:
                                description = "¿Qué número falta en esta operación? " + number1 + " + ... + " + number3 + " = " + (number1 + number2 + number3);
                                result = number2;
                                showHint = false;
                                break;
                            case 3:
                                description = "Tengo una moneda de " + number1 + " céntimos, otra de " + number2 + " céntimos y otra de " + number3 + " céntimos, ¿Cuántos céntimos tengo en total?";
                                result = number1 + number2 + number3;
                                usesThirdNumber = true;
                                imageID = R.drawable.coin;
                                break;
                            case 4:
                                number1 = Utils.randomNumberMinMax(1, 21);
                                description = "Si hoy es lunes " + number1 + " de mayo, ¿qué día será el martes que viene?";
                                result = number1 + 8;
                                showHint = false;
                                imageID = R.drawable.calendar;
                                break;
                        }
                        break;
                    //endregion
                }
                if (result == 0) {
                    result = number1 + number2;
                }
                break;
            //endregion
            //region Subtract
            case SUBTRACT:
                if (number1 < number2) {
                    int aux = number1;
                    number1 = number2;
                    number2 = aux;
                }
                if (number1 == number2) {
                    number1++;
                }
                switch (difficultyLevel) {
                    //region Level 1
                    case FACIL:
                        switch (seed) {
                            case 0:
                            case 1:
                            case 2:
                                description = "Tenía " + number1 + " " + food[foodIndex] + " y me como " + number2 + ", ¿cuántas me quedan?";
                                imageID = foodDrawables[foodIndex];
                                multipleAnswers = true;
                                possibleAnswers = generateAnswers("Me quedan %d %s", food[foodIndex], operation, difficultyLevel, number1, number2);
                                break;
                            case 3:
                            case 4:
                                description = "Hay " + number1 + " " + food[foodIndex] + ", si me como " + number2 + ", ¿cuántas quedan?";
                                imageID = foodDrawables[foodIndex];
                                multipleAnswers = true;
                                possibleAnswers = generateAnswers("Quedan %d %s", food[foodIndex], operation, difficultyLevel, number1, number2);
                                break;
                        }
                        break;
                    //endregion
                    //region Level 2
                    case 1:
                        switch (seed) {
                            case 0:
                                description = context.getResources().getString(R.string.problem6, number1 - number2);
                                multipleAnswers = true;
                                possibleAnswers.add(new Answer(context.getResources().getString(R.string.answer6, number1, number2), number1 - number2));
                                aux1 += 2;
                                possibleAnswers.add(new Answer(context.getResources().getString(R.string.answer6, aux1, aux2), aux1 - aux2));
                                aux2 -= 2;
                                aux1--;
                                possibleAnswers.add(new Answer(context.getResources().getString(R.string.answer6, aux1, aux2), aux1 - aux2));
                                break;

                            case 1:
                                multipleAnswers = true;
                                description = "Hay " + (number1 + number2) + " " + food[foodIndex] + ". Si " + peopleNames[nameIndex] + " y " + peopleNames[altNameIndex] + " se comen una cada uno, ¿qué cantidad de " + food[foodIndex] + " queda?";
                                possibleAnswers.add(new Answer(((aux1 + aux2) - 2) + " " + food[foodIndex] + " porque " + (aux1 + aux2) + " - 2 = " + ((aux1 + aux2) - 2), ((aux1 + aux2) - 2)));
                                aux1 += 1;
                                possibleAnswers.add(new Answer(((aux1 + aux2) - 2) + " " + food[foodIndex] + " porque " + (aux1 + aux2) + " - 2 = " + ((aux1 + aux2) - 2), ((aux1 + aux2) - 2)));
                                aux2 += 2;
                                aux1--;
                                possibleAnswers.add(new Answer(((aux1 + aux2) - 2) + " " + food[foodIndex] + " porque " + (aux1 + aux2) + " - 2 = " + ((aux1 + aux2) - 2), ((aux1 + aux2) - 2)));
                                result = ((number1 + number2) - 2);
                                imageID = foodDrawables[foodIndex];
                                break;

                            case 2:
                                description = "¿Cual de las respuestas NO es igual a " + (number1 - number2) + "?";
                                multipleAnswers = true;
                                possibleAnswers.add(new Answer(((number1 + 2) + " - " + (number2 + 1)), (number1 + 2) - (number2 - 1)));
                                possibleAnswers.add(new Answer((number1 + " - " + number2), number1 - number2));
                                possibleAnswers.add(new Answer(((number1 + 1) + " - " + (number2 + 1)), number1 - number2));
                                inverse = true;
                                break;

                            case 3:
                                description = peopleNames[nameIndex] + " tiene " + number1 + " " + food[foodIndex] + ". Si se come " + number2 + ", ¿cuál es la cantidad de " + food[foodIndex] + " que le queda?";
                                multipleAnswers = true;
                                possibleAnswers.add(new Answer((number1 - number2) + " " + food[foodIndex], number1 - number2));
                                aux1++;
                                possibleAnswers.add(new Answer((aux1 - aux2) + " " + food[foodIndex], aux1 - aux2));
                                aux2 += 2;
                                aux1--;
                                possibleAnswers.add(new Answer((aux1 - aux2) + " " + food[foodIndex], aux1 - aux2));
                                imageID = foodDrawables[foodIndex];
                                break;

                            case 4:
                                description = "Si tengo " + number1 + " " + objects[objectsIndex] + " y " + number2 + " son de color " + colours[colorIndex] + ", ¿cuál es la cantidad de " + objects[objectsIndex] + " de otros colores?";
                                imageID = objectDrawables[objectsIndex];
                                multipleAnswers = true;
                                possibleAnswers = generateAnswers(null, objects[objectsIndex], operation, difficultyLevel, number1, number2);
                                break;

                        }
                        break;
                    //endregion
                    //region Level 3
                    case DIFICIL:
                        switch (seed) {
                            case 0:
                                description = "Un bus vacío recogió a " + number1 + " personas. En la primera parada bajaron " + number2 + ". ¿Cuántas personas quedaron?";
                                imageID = R.drawable.bus;
                                break;
                            case 1:
                                description = "Tengo un puzzle de " + number1 + " piezas. Ya he colocado " + number2 + ". ¿Cuántas me faltan por colocar?";
                                imageID = R.drawable.puzzle;
                                break;
                            case 2:
                                description = peopleNames[nameIndex] + " tiene una novela de " + number1 + " páginas, si ya ha leído " + number2 + " páginas, ¿cuántas páginas le quedan para terminar?";
                                imageID = R.drawable.book;
                                break;
                            case 3:
                                description = peopleNames[nameIndex] + " necesita " + number1 + " puntos para pasasr al nivel 2 de un juego. Sólo tiene " + number2 + " puntos, ¿cuántos puntos le faltan?";
                                imageID = R.drawable.videogame;
                                break;
                            case 4:
                                description = "Toda la clase fue de excursión menos " + peopleNames[Utils.randomNumber(peopleNames.length)] + ", " + peopleNames[Utils.randomNumber(peopleNames.length)] + " y " + peopleNames[Utils.randomNumber(peopleNames.length)] + ". Si en la clase hay " + number1 + " alumnos, ¿cuántos fueron de excursión?";
                                imageID = R.drawable.excursion;
                                result = number1 - 3;
                                break;
                        }
                        break;
                    //endregion
                }
                if (result == 0) {
                    result = number1 - number2;
                }
                break;
            //endregion
            //region Multiplication
            case MULTIPLICATION: {
                switch (difficultyLevel) {
                    case FACIL: {
                        switch (seed) {
                            case 0:
                                description = "¿Cuál es el resultado de " + number1 + " x " + number2 + "?";
                                break;

                            case 1:
                                description = "Un paquete contiene " + number1 + " cromos. ¿Cuántos cromos tendré comprando " + number2 + " paquetes?";
                                imageID = R.drawable.cromos;
                                multipleAnswers = true;
                                possibleAnswers = generateAnswers(null, "cromos", operation, difficultyLevel, number1, number2);
                                break;

                            case 2:
                                description = "Una señora compró " + number1 + " paquetes con " + number2 + " " + food[foodIndex] + " cada uno, para llevar a una fiesta, ¿Qué cantidad de " + food[foodIndex] + " llevará a la fiesta?";
                                imageID = foodDrawables[foodIndex];
                                multipleAnswers = true;
                                possibleAnswers = generateAnswers(null, "paquetes", operation, difficultyLevel, number1, number2);
                                break;

                            case 3:
                                description = "Una familia gasta " + number1 + " euros al día. ¿Cuánto gastará en una semana?";
                                number2 = 7;
                                imageID = R.drawable.coin;
                                multipleAnswers = true;
                                possibleAnswers = generateAnswers("Gastan %d %s", "euros", operation, difficultyLevel, number1, number2);
                                break;

                            case 4:
                                description = peopleNames[nameIndex] + " gasta $" + number1 + " pesos todos los días en el camión que lo lleva a la escuela y lo trae a la casa, ¿Cuánto gasta a la semana?";
                                number2 = 7;
                                imageID = R.drawable.bus;
                                multipleAnswers = true;
                                possibleAnswers = generateAnswers("Gasta $%d %s a la semana", "pesos", operation, difficultyLevel, number1, number2);
                                break;

                        }
                        break;
                    }
                    case MEDIO: {
                        switch (seed) {
                            case 0:
                                description = "Dentro de " + number1 + " semanas tengo vacaciones. ¿Cuántos días tengo que esperar?";
                                number2 = 7;
                                imageID = R.drawable.calendar;
                                multipleAnswers = true;
                                possibleAnswers = generateAnswers(null, "días", operation, difficultyLevel, number1, number2);
                                break;

                            case 1:
                                description = peopleNames[nameIndex] + " quiere contar las patas de " + number1 + " abejas. Si cada insecto tiene seis patas, ¿cuántas patas contará en total?";
                                number2 = 6;
                                imageID = R.drawable.bee;
                                multipleAnswers = true;
                                possibleAnswers = generateAnswers(null, "patas", operation, difficultyLevel, number1, number2);
                                break;

                            case 2:
                                description = "¿Cuál es el triple de " + number1 + "?";
                                number2 = 3;
                                break;

                            case 3:
                                description = "Si mi hermano gemelo tiene " + number1 + " años, ¿Cuántos años tenemos entre los dos?";
                                number2 = 2;
                                multipleAnswers = true;
                                possibleAnswers = generateAnswers("Tenemos %d %s", "años", operation, difficultyLevel, number1, number2);
                                break;

                            case 4:
                                number1 *= 10;
                                description = "En un terreno se desea plantar " + number1 + " árboles por cada héctarea. ¿Cuántos se tendrá que plantar si el terreno mide " + number2 + " héctareas?";
                                imageID = R.drawable.tree;
                                multipleAnswers = true;
                                possibleAnswers = generateAnswers("Se tienen que plantar %d %s", "árboles", operation, difficultyLevel, number1, number2);
                                break;
                        }
                        break;
                    }
                    case DIFICIL: {
                        switch (seed) {
                            case 0:
                                description = "Si un par de deportivas valen " + number1 + " euros, ¿cuánto valdrán " + number2 + " pares?";
                                imageID = R.drawable.shoe;
                                break;

                            case 1:
                                description = "¿Cuál es el resultado de " + number1 + " x " + number2 + "?";
                                break;

                            case 2:
                                description = "En una granja se recogen " + number1 + " huevos diariamente, ¿Cuántos huevos se recogerán en total en " + number2 + " días?";
                                imageID = R.drawable.farm;
                                break;

                            case 3:
                                description = "Una caja contiene " + number1 + " " + food[foodIndex] + ". Si el peso medio de una unidad es de " + number2 + "gr. ¿Cuántos kg pesará la caja de " + food[foodIndex] + "?";
                                imageID = foodDrawables[foodIndex];
                                break;

                            case 4:
                                number1 *= 10;
                                description = "Si mi sueldo es de " + number1 + " euros al mes ¿Cuánto ganaré al año?";
                                imageID = R.drawable.coin;
                                break;
                        }
                        break;
                    }
                }
                result = number1 * number2;
                break;
            }
            //endregion
            //region Division
            case DIVISION:
                if (number2 % 2 != 0) {
                    number2++;
                }
                number1 = number2 * Utils.randomNumberMinMax(2, 8);
                switch (difficultyLevel) {
                    case FACIL: {
                        switch (seed) {
                            case 0:
                                description = peopleNames[nameIndex] + " ha contado " + number1 + " patas de gatos en el parque. ¿Cuántos gatos había allí?";
                                number2 = 4;
                                imageID = R.drawable.cat;
                                multipleAnswers = true;
                                possibleAnswers = generateAnswers("Había %d %s", "gatos", operation, difficultyLevel, number1, number2);
                                break;

                            case 1:
                                description = peopleNames[nameIndex] + "  quiere leer un cuento de " + number1 + " páginas en vacaciones. Si estas duran " + number2 + " semanas, ¿cuántas páginas debe leer cada semana?";
                                imageID = R.drawable.book;
                                multipleAnswers = true;
                                possibleAnswers = generateAnswers(null, "páginas", operation, difficultyLevel, number1, number2);
                                break;

                            case 2:
                                description = "Cada manzana la hemos partido en " + number2 + " trozos. Si hay " + number1 + " trozos, ¿cuántas manzanas he cortado?";
                                imageID = R.drawable.apple;
                                multipleAnswers = true;
                                possibleAnswers = generateAnswers(null, "manzanas", operation, difficultyLevel, number1, number2);
                                break;

                            case 3:
                                description = "Un depósito contiene " + number1 + " litros de agua. Si se reparte todo el agua en recipientes de " + number2 + " litros cada uno, ¿cuántos recipientes se llenarán de agua?";
                                imageID = R.drawable.water;
                                multipleAnswers = true;
                                possibleAnswers = generateAnswers(null, "recipientes", operation, difficultyLevel, number1, number2);
                                break;

                            case 4:
                                description = "De paseo por el bosque, nos comimos " + number1 + " " + food[foodIndex] + ". Si íbamos " + number2 + " personas, ¿qué cantidad de " + food[foodIndex] + " se comió cada uno?";
                                imageID = foodDrawables[foodIndex];
                                multipleAnswers = true;
                                possibleAnswers = generateAnswers(null, food[foodIndex], operation, difficultyLevel, number1, number2);
                                break;
                        }
                        break;
                    }
                    case MEDIO: {
                        switch (seed) {
                            case 0:
                                description = "Quiero poner " + number1 + " fotos en un álbum. Si el álbum tiene " + number2 + " páginas y quiero poner en cada página el mismo número de fotos, ¿cuántas podré poner?";
                                imageID = R.drawable.book;
                                multipleAnswers = true;
                                possibleAnswers = generateAnswers(null, "fotos", operation, difficultyLevel, number1, number2);
                                break;

                            case 1:
                                description = "Hay " + number1 + " niños en un aula. ¿Cuántos grupos de " + number2 + " se pueden hacer?";
                                imageID = R.drawable.excursion;
                                multipleAnswers = true;
                                possibleAnswers = generateAnswers("Se pueden hacer %d %s de niños", "grupos", operation, difficultyLevel, number1, number2);
                                break;

                            case 2:
                                description = "La mitad de " + number1 + " es: ";
                                number2 = 2;
                                break;

                            case 3:
                                description = "En una tienda tienen un total de " + number1 + " " + food[foodIndex] + ". Si se desea hacer paquetes de " + number2 + " " + food[foodIndex] + ", ¿cuántos paquetes se podrán hacer?";
                                imageID = foodDrawables[foodIndex];
                                multipleAnswers = true;
                                possibleAnswers = generateAnswers("%d paquetes de %s", food[foodIndex], operation, difficultyLevel, number1, number2);
                                break;

                            case 4:
                                description = "Una maestra quiere repartir " + number1 + " dulces entre sus alumnos. Si hay " + number2 + " alumnos en el salón, ¿cuántos dulces le tocará a cada uno?";
                                imageID = R.drawable.candies;
                                multipleAnswers = true;
                                possibleAnswers = generateAnswers("Le tocarán %d %s a cada uno", "dulces", operation, difficultyLevel, number1, number2);
                                break;
                        }
                        break;
                    }
                    case DIFICIL: {
                        switch (seed) {
                            case 0:
                                description = "El teatro del colegio dispone de " + number1 + " asientos. Si están repartidos de forma equitativa en " + number2 + " líneas, ¿Cuántos asientos habrá por línea?";
                                imageID = R.drawable.theater;
                                break;

                            case 1:
                                description = "¿Cuántos billetes de " + number2 + " € hay como máximo en " + number1 + " €?";
                                imageID = R.drawable.coin;
                                break;

                            case 2:
                                description = "En mi patio hay " + number1 + " árboles. Si un cuarto son pinos, ¿cuántos pinos hay?";
                                number2 = 4;
                                imageID = R.drawable.tree;
                                break;

                            case 3:
                                description = "De paseo por el desierto, nos comimos " + number1 + " " + food[foodIndex] + ". Si íbamos " + number2 + " personas, ¿qué cantidad de " + food[foodIndex] + " se comió cada uno?";
                                imageID = foodDrawables[foodIndex];
                                break;

                            case 4:
                                description = "Si hay " + number1 + " libros colocados en " + number2 + " estanterías, ¿cuántos libros hay en cada estantería?";
                                imageID = R.drawable.book;
                                break;
                        }
                        break;
                    }
                }
                result = number1 / number2;
                break;
            //endregion
        }
        if (showHint) {
            if (!multipleAnswers) {
                description += "\n\n" +
                        "Resuelve: " + number1 + operator + number2;
                if (usesThirdNumber) {
                    description += operator + number3;
                }
            }
        }
        return new Problem(description, result, operation, inverse, multipleAnswers, possibleAnswers, imageID);
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SUM, SUBTRACT, MULTIPLICATION, DIVISION})
    public @interface Category {
    }

    private List<Answer> generateAnswers(@Nullable String text, String object, int operation, int level, int number1, int number2) {
        List<Answer> possibleAnswers = new ArrayList<>();

        possibleAnswers.add(0, generateAnswer(text, object, operation, level, number1, number2));
        if (operation == DIV) {
            int newNumber1 = number1;
            while (newNumber1 == number1) {
                newNumber1 = number2 * Utils.randomNumberMinMax(1, 8);
            }

            possibleAnswers.add(1, generateAnswer(text, object, operation, level, newNumber1, number2));
            int oldNumber1 = newNumber1;
            newNumber1 = number1;
            while (newNumber1 == number1 || newNumber1 == oldNumber1) {
                newNumber1 = number2 * Utils.randomNumberMinMax(1, 8);
            }

            possibleAnswers.add(2, generateAnswer(text, object, operation, level, newNumber1, number2));
        } else {
            possibleAnswers.add(1, generateAnswer(text, object, operation, level, number1 + 1, number2));
            possibleAnswers.add(2, generateAnswer(text, object, operation, level, number1 + 2, number2 + 1));
        }


        return possibleAnswers;
    }

    private Answer generateAnswer(@Nullable String text, String object, int operation, int level, int number1, int number2) {
        Answer answer = new Answer();

        int result = 0;
        String operator = "";
        switch (operation) {
            case SUM:
                result = number1 + number2;
                operator = " + ";
                break;
            case SUB:
                result = number1 - number2;
                operator = " - ";
                break;
            case MULT:
                result = number1 * number2;
                operator = " × ";
                break;
            case DIV:
                result = number1 / number2;
                operator = " ÷ ";
                break;
        }

        if (text != null) {
            text = String.format(text, result, object);
        } else {
            text = result + " " + object;
        }

        if (level == 0) {
            text += ". Porque " + number1 + operator + number2 + " = " + result;
        }

        answer.setDesc(text);
        answer.setResult(result);

        return answer;
    }
}

