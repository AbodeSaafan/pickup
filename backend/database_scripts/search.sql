CREATE OR REPLACE FUNCTION distance(pt1 POINT, pt2 POINT) RETURNS FLOAT AS $$
DECLARE                                                   
    x float = 111.12 * (pt2[1] - pt1[1]);                           
    y float = 111.12 * (pt2[0] - pt1[0]) * cos(pt1[1] / 92.215);        
BEGIN                                                     
    RETURN sqrt(x * x + y * y);                               
END  
$$ LANGUAGE plpgsql;

/*https://stackoverflow.com/questions/10034636/postgresql-latitude-longitude-query*/