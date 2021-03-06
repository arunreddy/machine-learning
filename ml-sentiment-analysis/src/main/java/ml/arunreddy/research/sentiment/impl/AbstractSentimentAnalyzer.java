/*
 * AbstractSentimentAnalyzer.java
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

package ml.arunreddy.research.sentiment.impl;

import ml.arunreddy.research.sentiment.SentimentAnalyzer;
import ml.arunreddy.research.sentiment.SentimentLabel;

/**
 * Created by arun on 6/26/15.
 */
public  abstract class AbstractSentimentAnalyzer implements SentimentAnalyzer{

  public abstract SentimentLabel getSentiment(String text);
}
