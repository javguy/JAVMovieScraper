package moviescraper.doctord.controller;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.model.Movie;
import moviescraper.doctord.view.GUIMain;

public class ScrapeSpecificAction extends AbstractAction {
	
	private static final long serialVersionUID = 1L;
	
	private GUIMain guiMain;
	private SiteParsingProfile profile;
	
	public ScrapeSpecificAction(GUIMain main, SiteParsingProfile profile) {
		this.guiMain = main;
		this.profile = profile;
		
		putValue(ScrapeMovieAction.SCRAPE_KEY, profile.getClass().getName());
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		try
		{
			guiMain.setMainGUIEnabled(false);
			guiMain.removeOldScrapedMovieReferences();
			List<File> toScrape = guiMain.getCurrentFile();
			List<File> noMovieFoundList = new LinkedList<File>();
			List<File> foundMovieMatch = new LinkedList<File>();
			if (toScrape != null) {
				for(File currentFile : toScrape)
				{ 
					//reset the SiteParsingProfile so we don't get leftover stuff from the last file scraped
					//we want it to be of the same type, so we use the newInstance() method which will automatically
					//return a new object of the type the SiteParsingProfile actually is
					SiteParsingProfile spp = profile.newInstance();
					spp.setScrapingLanguage(guiMain.getPreferences());
					SpecificScraperAction action = new SpecificScraperAction(spp, spp.getMovieScraper(), currentFile );
					Movie scrapedMovie = action.scrape(guiMain.getPreferences());
					if(scrapedMovie != null)
					{
						guiMain.movieToWriteToDiskList.add(scrapedMovie);
						guiMain.getFileDetailPanel().setNewMovie( scrapedMovie , true);
						foundMovieMatch.add(currentFile);
					}
					else
					{
						guiMain.movieToWriteToDiskList.add(null);
						noMovieFoundList.add(currentFile);
					}
					
				}
				//Display a list of all the movies not found while scraping if we didn't match any of the files
				if(noMovieFoundList.size() == toScrape.size())
					JOptionPane.showMessageDialog(guiMain.getFrmMoviescraper(), 
							"No Matches found for: \n " + noMovieFoundList, 
							"No Movies Found", JOptionPane.ERROR_MESSAGE);
				else
				{
					System.out.println("Scraper found matches for " + foundMovieMatch.size() + "/" +
						toScrape.size() + " selected files.");
					if(foundMovieMatch.size() > 0)
						System.out.println("Files scraper found a match for: " + foundMovieMatch);
					if(noMovieFoundList.size() > 0)
					System.out.println("Files scraper did not find a match for: " + noMovieFoundList);
				}

			} else {
				JOptionPane.showMessageDialog(guiMain.getFrmMoviescraper(), "No file selected.", "No file selected.", JOptionPane.ERROR_MESSAGE);
			}
		}
		finally
		{
			guiMain.setMainGUIEnabled(true);
		}
	}
}
