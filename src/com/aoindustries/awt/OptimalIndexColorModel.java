package com.aoindustries.awt;

/*
 * Copyright 2000-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates a 255 + alpha IndexColorModel to best match a BufferedImage.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class OptimalIndexColorModel extends IndexColorModel {

    static class ColorCount {

        int color;
        int count;
        
        ColorCount(int color) {
            this.color = color;
        }

        public boolean equals(Object O) {
            return
                O!=null
                && (O instanceof ColorCount)
                && ((ColorCount)O).color==color
            ;
        }
        
        public int hashCode() {
            return color;
        }
    }

    /**
     * Sorted by count descending.
     */
    static class ColorCountCountComparator implements Comparator<ColorCount> {

        public int compare(ColorCount o1, ColorCount o2) {
            return o1.count-o2.count;
        }

        public boolean equals(Object obj) {
            return obj!=null && (obj instanceof ColorCountCountComparator);
        }
    }

    /**
     * The minimum brightness for a color to be dropped.
     */
    private static final int DROP_THRESHOLD=96;

    /**
     * The offset used to keep the dark colors in the image.
     */
    private static final int DROP_OFFSET=1000;

    private OptimalIndexColorModel(int size, byte[] r, byte[] g, byte[] b, int trans) {
	super(8, size, r, g, b, trans);
    }

    public static OptimalIndexColorModel getOptimalIndexColorModel(BufferedImage image) {
        // Sorted by color
        Map<Integer,ColorCount> colorCounts = new HashMap<Integer,ColorCount>();

	// Count the use of each color in the image
	int width=image.getWidth();
	int height=image.getHeight();
	for(int y=0;y<height;y++) {
	    for(int x=0;x<width;x++) {
		Integer color=Integer.valueOf(image.getRGB(x,y));
                ColorCount colorCount = colorCounts.get(color);
                if(colorCount==null) colorCounts.put(color, colorCount=new ColorCount(color.intValue()));
                colorCount.count++;
	    }
	}

	// Add 1000 to the counts for those less than DROP_THRESHOLD brightness
        for(ColorCount colorCount : colorCounts.values()) {
	    int color=colorCount.color;
	    int brightness=Math.max(
                (color>>>16)&255,
                Math.max(
                    (color>>>8)&255,
                    color&255
                )
            );
	    if(brightness<DROP_THRESHOLD) colorCount.count+=DROP_OFFSET;
	}

	// Sort the list based on number of times the pixels were used (most used on top)
        List<ColorCount> colorCountList = new ArrayList<ColorCount>(colorCounts.values());
        Collections.sort(colorCountList, new ColorCountCountComparator());

	// Use at most the top 256 colors
        int size=colorCountList.size();
	int numColorsUsed=size>256?256:size;
	byte[] r=new byte[numColorsUsed];
	byte[] g=new byte[numColorsUsed];
	byte[] b=new byte[numColorsUsed];

	int transparent_index=-1;

        for(int c=0;c<numColorsUsed;c++) {
            int color=colorCountList.get(c).color;
            byte red=r[c]=(byte)((color>>>16)&255);
            byte green=g[c]=(byte)((color>>>8)&255);
            byte blue=b[c]=(byte)(color&255);
            // TODO: transparency not distinguished properly: if(transparent_index==-1 && red==-1 && green==-1 && blue==-1) transparent_index=c;
	}
	return new OptimalIndexColorModel(numColorsUsed, r, g, b, transparent_index);
    }
}
