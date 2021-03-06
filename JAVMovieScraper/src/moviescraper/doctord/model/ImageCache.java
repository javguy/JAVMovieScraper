package moviescraper.doctord.model;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import moviescraper.doctord.controller.FileDownloaderUtilities;

public class ImageCache {
	private static Map<URL, Image> cache = Collections.synchronizedMap(new HashMap<URL, Image>());
	
	public static Image getImageFromCache(URL url) throws IOException
	{
		//Cache already contains the item, so just return it
		if(cache.containsKey(url))
		{
			return cache.get(url);
		}
		//we didn't find it, so read the Image into the cache and also return it
		else
		{
			try{
				if(url != null)
				{
					Image imageFromUrl = FileDownloaderUtilities.getImageFromUrl(url);
					if(imageFromUrl != null)
					{
						cache.put(url, imageFromUrl);
						return imageFromUrl;
					}
				}

					//we couldn't read in the image from the URL so just return a blank image
					
					Image blankImage = createBlankImage();
					cache.put(url, blankImage);
					return blankImage;
			}
			catch (OutOfMemoryError e) {
                System.out.println("We ran out of memory..clearing the cache. It was size " + cache.size() 
                		+ " before the clear");
                cache.clear();
                return FileDownloaderUtilities.getImageFromUrl(url);
            }
			catch(IOException e)
			{
				e.printStackTrace();
				Image blankImage = createBlankImage();
				cache.put(url, blankImage);
				return blankImage;
			}
		}
	}
	
	public static void putImageInCache(URL url, Image image)
	{
		//300 is arbitrary for now, but at some point we gotta boot stuff from cache or we will run out memory
		//Ideally, I would boot out old items first, but I would need a new data structure to do this, probably
		//by using a library already written that handles all the cache stuff rather than just using a map like I'm doing
		//in this class
		if(cache.size() > 300)
		{
			System.out.println("Clearing cache - cache had " + cache.size() + " items before clearing.");
			cache.clear();
		}
		cache.put(url, image);
	}
	
	private static Image createBlankImage()
	{
		return new BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB);
	}
	
	public static void removeImageFromCachce(URL url)
	{
		cache.remove(url);
	}
	
	public static boolean isImageCached(URL url)
	{
		return cache.containsKey(url);
	}
}
