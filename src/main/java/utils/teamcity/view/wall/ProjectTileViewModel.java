package utils.teamcity.view.wall;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.Subscribe;
import com.google.inject.assistedinject.Assisted;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.scene.layout.Background;
import utils.teamcity.model.build.BuildStatus;
import utils.teamcity.model.build.ProjectData;
import utils.teamcity.model.build.ProjectManager;
import utils.teamcity.model.configuration.Configuration;

import javax.inject.Inject;
import java.util.Set;

/**
 * Date: 22/02/14
 *
 * @author Cedric Longo
 */
final class ProjectTileViewModel {

    private final ProjectManager _projectManager;
    private final ProjectData _projectData;

    private final StringProperty _displayedName = new SimpleStringProperty( );
    private final ObjectProperty<Background> _background = new SimpleObjectProperty<>( );

    private final BooleanProperty _lightMode = new SimpleBooleanProperty( );

    interface Factory {
        ProjectTileViewModel forProjectData( final ProjectData projectData );
    }

    @Inject
    ProjectTileViewModel( final Configuration configuration, final ProjectManager projectManager, @Assisted final ProjectData projectData ) {
        _projectManager = projectManager;
        _projectData = projectData;
        updateConfiguration( configuration );
        updateProjectViewModel( projectData );
    }

    @Subscribe
    public final void updateProjectViewModel( final ProjectData data ) {
        final Set<ProjectData> allProjects = getAllInterestingProjects( );

        if ( !allProjects.contains( data ) )
            return;

        Platform.runLater( ( ) -> {
            _displayedName.set( Strings.isNullOrEmpty( data.getAliasName( ) ) ? data.getName( ) : data.getAliasName( ) );
            updateBackground( );
        } );
    }

    @Subscribe
    public void updateConfiguration( final Configuration configuration ) {
        Platform.runLater( ( ) -> {
            _lightMode.setValue( configuration.isLightMode( ) );
        } );
    }

    private void updateBackground( ) {
        final Set<ProjectData> allProjects = getAllInterestingProjects( );

        final int failureCount = allProjects.stream( ).map( p -> _projectData.getBuildTypeCount( BuildStatus.FAILURE, BuildStatus.ERROR ) ).reduce( 0, Integer::sum );
        final int successCount = allProjects.stream( ).map( p -> _projectData.getBuildTypeCount( BuildStatus.SUCCESS ) ).reduce( 0, Integer::sum );
        final int unknownCount = allProjects.stream( ).map( p -> _projectData.getBuildTypeCount( BuildStatus.UNKNOWN ) ).reduce( 0, Integer::sum );

        if ( unknownCount > 0 || failureCount + successCount == 0 ) {
            _background.setValue( BuildBackground.UNKNOWN.getMain( ) );
            return;
        }

        // Setting main background according to failure count
        _background.setValue( failureCount == 0 ? BuildBackground.SUCCESS.getMain( ) : BuildBackground.FAILURE.getMain( ) );
    }


    private Set<ProjectData> getAllInterestingProjects( ) {
        return ImmutableSet.<ProjectData>builder( )
                .add( _projectData )
                .addAll( _projectManager.getAllChildrenOf( _projectData ) )
                .build( );
    }

    String getDisplayedName( ) {
        return _displayedName.get( );
    }

    StringProperty displayedNameProperty( ) {
        return _displayedName;
    }

    Background getBackground( ) {
        return _background.get( );
    }

    ObjectProperty<Background> backgroundProperty( ) {
        return _background;
    }

    BooleanProperty lightModeProperty( ) {
        return _lightMode;
    }

    public boolean isLightMode( ) {
        return _lightMode.get( );
    }

}
