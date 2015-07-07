/*
 * SentimentLabel.java
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

import com.ohmdb.api.Db;
import com.ohmdb.api.Ohm;

/**
 * Created by arun on 6/26/15.
 */
public class SentimentLabel {

	private int sentimentLabelId;

	public static final SentimentLabel POSITIVE = new SentimentLabel(1);

	public static final SentimentLabel NEGATIVE = new SentimentLabel(-1);

	public static final SentimentLabel NEUTRAL = new SentimentLabel(0);

	public SentimentLabel(int label) {
		this.sentimentLabelId = label;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		SentimentLabel that = (SentimentLabel) o;

		return sentimentLabelId == that.sentimentLabelId;

	}

	@Override
	public int hashCode() {
		return sentimentLabelId;
	}

	@Override
	public String toString() {
		return "SentimentLabel [sentimentLabelId=" + sentimentLabelId + "]";
	}

}
