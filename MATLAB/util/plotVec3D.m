function plotVec3D(x,y,z)

    mag = norm([x,y,z]);
    hold on;
    plot3( [0 x],[0 0], [0 0], 'r-x'  );
    text( x,0,0, sprintf('%.1f', x)  );

    plot3( [0 0],[0 y], [0 0], 'g-x'  );
    text( 0,y,0, sprintf('%.1f', y)  );

    plot3( [0 0],[0 0], [0 z], 'b-x'  );
    text( 0,0,z, sprintf('%.1f', z)  );

    plot3([0,x], [0,y],[0,z], 'm--x');
    text(x,y,z,sprintf('|%.1f|', mag));
    
    hold off;

    


end