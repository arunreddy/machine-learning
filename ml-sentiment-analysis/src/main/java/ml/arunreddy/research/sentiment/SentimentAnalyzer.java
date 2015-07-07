/*
 * SentimentAnalyzer.java
 *
 * Copyright (c) 2015  Arun Reddy Nelakurthi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Report bugs or new features to: arunreddy@asu.edu
 */

package ml.arunreddy.research.sentiment;

/**
 * Created by arun on 6/26/15.
 */
public interface SentimentAnalyzer {


  /**
   * Returns the sentiment of the text as a String.
   *
   * @param text
   * @return sentiment label of the text, @see SentimentLabel
   */
  SentimentLabel getSentiment(String text);

}
