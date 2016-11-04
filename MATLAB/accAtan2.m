init;
data = loadCSVAndPreprocess('../Data/20161002_120303_426.csv');


grav = zeros(size(data,1),3);
alpha = 0.9;

box = makeBox(5,10,4);

for i=2:size(data,1)
    dt = (data(i,1)-data(i-1,1))/1000;
    beta = alpha-dt;
    grav(i,:) = grav(i-1,:).*beta + (1-beta).*data(i,[ACCX,ACCY,ACCZ]);
    
    omega = grav(i,:)./norm(grav(i,:));
    
    angle = [ atan2(omega(3), omega(2)), atan2(omega(1),omega(3)), atan2(omega(2),omega(1)) ];
    R = rotXYZ( angle(1), angle(2), angle(3) );
    boxr = (R*box')';
    
    if(mod(i,10)==0)
        figure(1)
        plot(0,0);
        plotVec3D( grav(i,1), grav(i,2), grav(i,3) );
        hold on;
        plot3( boxr(:,1), boxr(:,2), boxr(:,3), 'b-o' );
        hold off;
        grid on;
        axis equal;
        axis([-10,10,-10,10,-10,10]);
%        view(2)
        drawnow;
    end
end

