package gg.galaxygaming.projectextended.client.lang;

import gg.galaxygaming.projectextended.client.lang.FormatSplitter.Component;
import gg.galaxygaming.projectextended.client.lang.FormatSplitter.FormatComponent;
import gg.galaxygaming.projectextended.client.lang.FormatSplitter.MessageFormatComponent;
import java.text.ChoiceFormat;
import java.util.List;
import moze_intel.projecte.PECore;
import net.minecraft.data.DataGenerator;

/**
 * @apiNote From Mekanism
 */
public class UpsideDownLanguageProvider extends ConvertibleLanguageProvider {

    public UpsideDownLanguageProvider(DataGenerator gen, String modid) {
        super(gen, modid, "en_ud");
        //Note: This technically is supposed to be upside down british english, but we are doing it as upside down US english
    }

    @Override
    public void convert(String key, List<Component> splitEnglish) {
        add(key, convertComponents(splitEnglish));
    }

    private static final String normal = "abcdefghijklmnopqrstuvwxyz" +
                                         "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                                         "0123456789" +
                                         ",.?!;\"'`&_^()[]{}<>";
    private static final char[] upside_down = ("\u0250q\u0254p\u01dd\u025f\u1d77\u0265\u1d09\u027e\u029e\ua781\u026fuodb\u0279s\u0287n\u028c\u028dx\u028ez" +
                                               "\u2c6f\u15fa\u0186\u15e1\u018e\u2132\u2141HI\u0550\ua7b0\ua780WNO\u0500\ua779\u1d1aS\u27d8\u2229\u039bMX\u2144Z" +
                                               "0\u295d\u1614\u0190\u07c8\u03db9\u312586" +
                                               "'\u02d9\u00bf\u00a1\u061b\u201e,,\u214b\u203ev)(][}{><").toCharArray();

    private static char flip(char c) {
        int index = normal.indexOf(c);
        return index == -1 ? c : upside_down[index];
    }

    private static String convertFormattingComponent(FormatComponent component, int curIndex, int numArguments) {
        if (component instanceof MessageFormatComponent) {
            //Convert a MessageFormat styled formatting code
            return convertMessageFormatCode((MessageFormatComponent) component);
        }
        String formattingCode = component.contents();
        //Convert a % styled formatting code
        String ending;
        int storedIndex = curIndex;
        //A formatting code can have at most one $ and if it has one then it is the first "argument" after the %
        String[] split = formattingCode.split("\\$");
        if (split.length == 2) {
            //It already has an index, so read that as the stored index
            ending = split[1];
            storedIndex = Integer.parseInt(split[0].substring(1));
        } else {
            //No index stored in the formatting code
            ending = formattingCode.substring(1);
        }
        //Compare the index the argument currently has with the index it will have afterwards
        // If they are the same we don't need to include the index argument
        if (storedIndex == numArguments - curIndex + 1) {
            return "%" + ending;
        }
        return "%" + storedIndex + "$" + ending;
    }

    /**
     * Reverses order of inner arguments of a MessageFormat styled formatting code
     */
    private static String convertMessageFormatCode(MessageFormatComponent component) {
        String formatStyle = component.getFormatStyle();
        if (formatStyle != null && component.isChoice()) {
            //The formatting style is a choice, and we want to invert any "excess" text that is part of it
            String newFormatStyle = invertChoice(formatStyle);
            try {
                new ChoiceFormat(newFormatStyle);
            } catch (IllegalArgumentException e) {
                PECore.LOGGER.warn("Failed to convert '{}' to an upside down choice format. Got: '{}' which was invalid.", formatStyle, newFormatStyle);
                //Safety check for if we failed to convert it into a valid choice format just fallback to leaving the format as is
                return component.contents();
            }
            return "{" + component.getArgumentIndex() + "," + component.getFormatType() + "," + newFormatStyle + "}";
        }
        //If we don't have a style we don't need to invert it so just return what we have
        // or our style is not a choice as only choice's need to have further processing done
        return component.contents();
    }

    private static String invertChoice(String choice) {
        StringBuilder converted = new StringBuilder();
        StringBuilder literalBuilder = new StringBuilder();
        StringBuilder textBuilder = new StringBuilder();
        char[] exploded = choice.toCharArray();
        int leftBrackets = 0;
        boolean inLiteral = true;
        for (char c : exploded) {
            if (inLiteral) {
                literalBuilder.append(c);
                if (c == '#' || c == '<' || c == '\u2264') {
                    //#, <, and less than equal are valid comparisons for ChoiceFormat
                    // after we hit one, we are no longer in a literal though so mark it as such
                    inLiteral = false;
                    converted.append(literalBuilder);
                    literalBuilder = new StringBuilder();
                }
            } else {
                if (c == '{') {
                    leftBrackets++;
                } else if (c == '}') {
                    leftBrackets--;
                } else if (c == '|' && leftBrackets == 0) {
                    inLiteral = true;
                    //Note: We directly use MessageFormat because forge does not use MessageFormat at all if it has valid % formatting codes
                    converted.append(convertComponents(FormatSplitter.splitMessageFormat(textBuilder.toString())));
                    textBuilder = new StringBuilder();
                }
                if (inLiteral) {
                    literalBuilder.append(c);
                } else {
                    textBuilder.append(c);
                }
            }
        }
        if (inLiteral) {
            converted.append(literalBuilder);
        } else {
            //Note: We directly use MessageFormat because forge does not use MessageFormat at all if it has valid % formatting codes
            converted.append(convertComponents(FormatSplitter.splitMessageFormat(textBuilder.toString())));
        }
        return converted.toString();
    }

    private static String convertComponents(List<Component> splitText) {
        int numArguments = (int) splitText.stream().filter(component -> component instanceof FormatComponent).count();
        StringBuilder converted = new StringBuilder();
        int curIndex = numArguments;
        for (int i = splitText.size() - 1; i >= 0; i--) {
            Component component = splitText.get(i);
            if (component instanceof FormatComponent formatComponent) {
                //Insert the full code directly
                converted.append(convertFormattingComponent(formatComponent, curIndex--, numArguments));
            } else {
                //Convert each character to being upside down and then insert at end
                char[] toConvertArr = component.contents().toCharArray();
                for (int j = toConvertArr.length - 1; j >= 0; j--) {
                    converted.append(flip(toConvertArr[j]));
                }
            }
        }
        return new String(converted);
    }
}