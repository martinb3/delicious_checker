/*
 * Created on Apr 23, 2006
 *
 * TODO Nothing yet.
 */
package org.mbs3.deliciouschecker;

import del.icio.us.*;
import del.icio.us.beans.*;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.HttpVersion;
import java.util.Iterator;
import java.util.List;

/**
 * @author Martin Smith
 *
 * TODO None yet.
 */
public class DeliciousChecker
{

    private static final String username = "username";
    private static final String password = "secret";
    private static final String useragent = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)";
    
    /**
     * @param args
     */
    public static void main (String[] args)
    {
        System.setProperty("org.apache.commons.logging.Log","org.apache.commons.logging.impl.NoOpLog");
        System.out.println("Connecting to del.icio.us");
        Delicious connection = new Delicious(DeliciousChecker.username, DeliciousChecker.password);
        System.out.println("Getting post data for url verification");
        List allPosts = connection.getAllPosts(); Iterator allPostsIterator = allPosts.iterator();
        System.out.println("Received " + allPosts.size() + " different posts, checking each");
        
        HttpClient hc = new HttpClient();
        hc.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_0);
        hc.getParams().setParameter("http.socket.timeout", new Integer(1000));
        while(allPostsIterator.hasNext())
        {
            Post post = (Post)allPostsIterator.next();
            //System.out.println("Trying " + post.getHref());
            try 
            {
                HeadMethod hm = new HeadMethod(post.getHref());
                hm.setRequestHeader("User-agent", DeliciousChecker.useragent);
                hm.getParams().setParameter("http.socket.timeout", new Integer(5000));
                int response = hc.executeMethod(hm);
                if(response != 200)
                {
                    System.out.println(post.getDescription() + "(" + post.getHref() + ") returned HTTP " + response);
                    post.setTag(post.getTag() + " broken");
                }
            } 
            catch (Exception ex) 
            {
                System.out.println(post.getDescription() + "(" + post.getHref() + ") returned " + ex);
                post.setTag(post.getTag() + " exception");
            }
            
        }
    }
}
